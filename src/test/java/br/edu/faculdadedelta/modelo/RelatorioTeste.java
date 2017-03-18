package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

import org.hamcrest.core.IsInstanceOf;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class RelatorioTeste {

	private EntityManager em;
	private static final String CPF_PADRAO = "000.111.222-10";

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarTodosCliente(){
		salvarClientes(3);
		
		Criteria criteria = createCriteria(Cliente.class, "c");
		
		List<Cliente> clientes = criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.list();
		assertTrue("lista deve ter pelomeno 3", clientes.size() >=3);
		
		clientes.forEach(cliente -> assertFalse(cliente.isTransient()));
		
	}
	
	@Test
	public void deveConsultarMaiorIdCliente(){
		salvarClientes(3);
		
		Criteria criteria = createCriteria(Cliente.class, "c")
				.setProjection(Projections.max("c.id"));
		
		Long maiorId = (Long) criteria
				.setResultTransformer(criteria.PROJECTION)
				.uniqueResult();
		assertTrue("ID deve ser pelo menos 3 ", maiorId >= 3);

	}
	
	@Test
	public void deveConsultarVendasDaUltimaSemanna(){
		salvarVendas(3);
		
		Calendar ultimaSemana = Calendar.getInstance();
		ultimaSemana.add(Calendar.WEEK_OF_YEAR, -1);
		
		Criteria criteria = createCriteria(Venda.class, "v")
		
		.add(Restrictions.between("v.dateHora", ultimaSemana.getTime(), new Date()))
		.setProjection(Projections.rowCount());
		
		Long qtdVendas =  (Long) criteria
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.uniqueResult();
		assertTrue("qtd vendas pelo menos 3", qtdVendas >=3);
			
	}
	
	@Test
	public void deveConsultarNotbook(){
		salvarVendas(3);
Criteria criteria = createCriteria(Produto.class, "p")
		.add(Restrictions.in("p.nome", "Notebook","NetBook", "MacBook"))
		.addOrder(Order.asc("p.fabricante"));
		
	

List<Produto> notebook = criteria
.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
.list();
assertFalse("verifica se a quantidade de notbook é pelomenos 3", notebook.size() >=3);
notebook.forEach(produto-> assertFalse(produto.isTransient()));
	}

	@Test
	public void deveConsultarDezPrimeiroProdutos(){
		salvarProdutos(20);
		
		Criteria criteria = createCriteria(Produto.class, "p")
				.setFirstResult(1)
				.setMaxResults(10);
		
		List<Produto> produtos = criteria.list();
		
		assertFalse("deve ter prodtos", produtos.isEmpty());
		
		assertTrue("deve ter só 10 itens", produtos.size()==10);
		
		produtos.forEach(produto-> assertFalse(produto.isTransient()));
		
	}
	
	@Test
	public void deveConsultarQuantidadeVendasPorCliente(){
		Criteria criteria = createCriteria(Venda.class, "v")
				.createAlias("v.cliente", "c")
				.setProjection(Projections.rowCount())
				.add(Restrictions.eq("c.cpf", CPF_PADRAO))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		Long qtdCompras = (Long) criteria.uniqueResult();
		
		assertTrue("Deve ter pelo menos 3 comproas", qtdCompras >= 3);		
		
	}
	
	@Test
	public void deveConksultarProdutoContendoParteDoNome(){
		salvarProdutos(3);
		
		Criteria criteria = createCriteria(Produto.class, "p")
		.add(Restrictions.ilike("p.nome", "book", MatchMode.ANYWHERE));
		
		List<Produto> produtos = criteria.list();
				//.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				//.list();
		
		//assertTrue("Verifica se os produtos é pelomenos 3", produtos.size()>=3);
		//produtos.forEach(produto -> assertTrue(produto.isTransient()));
		assertFalse("Deve ter encontrado produtos", produtos.isEmpty());		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarNotbookDellouSamsung(){
		salvarProdutos(3);
		
		
		Criteria criteria = createCriteria(Produto.class, "p")
		.add(Restrictions.or(
				Restrictions.eqOrIsNull("p.fabricante", "Dell"),
				Restrictions.eqOrIsNull("p.fabricante", "Samsung")
				)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		List<Produto> produtos = criteria.list();
		assertFalse("deve ter notibook Dell e Sansung", produtos.isEmpty());
	}
	
@Test
public void deveConsultarVendasENomeClienteCasoExista(){
	salvarVendas(1);
	
	Criteria criteria = createCriteria(Venda.class,"v")
			.createAlias("v.cliente", "c",JoinType.LEFT_OUTER_JOIN )
			.add(Restrictions.ilike("c.nome", "Atila", MatchMode.START))
			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			
			
			List<Venda> vendas = criteria.list();
	assertFalse("Deve ter encontrado vendas pro Atila", vendas.isEmpty());
			
}

@Test
public void deveConsultarIdENomeProduto(){
	salvarProdutos(3);
	
	Projection projection = Projections.projectionList()
			.add(Projections.property("p.id").as("id"))
			.add(Projections.property("p.nome").as("nome"));
	
	Criteria criteria = createCriteria(Produto.class,"p")
			.setProjection(projection)
			.setResultTransformer(Criteria.PROJECTION);
	
	List<Object[]> produtos = criteria.list();

	assertTrue("verifica se a quantidade de produtos é pelo menos1", produtos.size() >=1);
	produtos.forEach(produto->{;
	assertTrue("primeiro item deve ser o ID", produto[0] instanceof Long);
	assertTrue("primeiro item deve ser o ID", produto[1] instanceof String);
});

}

@Test

public void deveConsultarIENomeProdutoEmMap(){
	
	salvarProdutos(3);
	
	ProjectionList projection = Projections.projectionList()
			.add(Projections.property("p.id").as ("id"))
			.add(Projections.property("p.nome").as("nome"));
	
	
	Criteria criteria = createCriteria(Produto.class, "p")
			.setProjection(projection)
			.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
	
	List<Map<String,Object>> produtos = criteria.list();
	assertFalse("deve ter produtos", produtos.isEmpty());
	
	produtos.forEach(clienteMap->{
		clienteMap.forEach((chave,valor)->{
			assertTrue("Chavede ser String", chave instanceof Strig);
			assertTrue("Chavede ser String ou Long", chave instanceof Strig || valor instanceof Long);
		});			
	});
	
	
}

	


		
	private Session getSession() {
		return (Session) em.getDelegate();
	}

	@SuppressWarnings("unused")
	private Criteria createCriteria(Class<?> clazz) {

		return getSession().createCriteria(clazz);

	}

	private Criteria createCriteria(Class<?> clazz, String alias) {
		return getSession().createCriteria(clazz, alias);
	}

	
	
	private void salvarClientes(int quantidade) {
		em.getTransaction().begin();
		for (int i = 0; i < quantidade; i++) {
			Cliente cliente = new Cliente();
			cliente.setNome("Atila Barros");
			cliente.setCpf(CPF_PADRAO);
			em.persist(cliente);
		}
		em.getTransaction().commit();

	}

	private void salvarProdutos(int quantidade) {
		em.getTransaction().begin();
		for (int i = 0; i < quantidade; i++) {
			Produto produto = criarProduto("Dell", "Samsung");

			em.persist(produto);
		}
		em.getTransaction().commit();

	}

	private void salvarVendas(int quantidade) {
		em.getTransaction().begin();

		for (int i = 0; i < quantidade; i++) {
			Venda venda = criarVenda();
			venda.getProdutos().add(criarProduto("notebook", "DELL"));
			venda.getProdutos().add(criarProduto("mouse", "Raze"));
			em.persist(venda);
		}
		em.getTransaction().commit();

	}

	@Before
	public void instaciarEntityManager() {
		em = JPAUtil.INSTANCE.getEntityManager();
	}

	@After
	public void fecharEntityManager() {
		if (em.isOpen()) {
			em.close();
		}
	}

	private Venda criarVenda() {
		return CriarVenda(null);
	}

	private Produto criarProduto(String nome, String marca) {
		Produto produto = new Produto();
		produto.setNome(nome);
		produto.setFabricante(marca);
		return produto;

	}

	private Venda CriarVenda(String cpf) {
		Cliente cliente = new Cliente();
		cliente.setNome("Atila Barros");
		cliente.setCpf(cpf == null ? CPF_PADRAO : cpf);

		assertTrue("nao deve ter ID definido", cliente.isTransient());

		Venda venda = new Venda();
		venda.setDateHora(new Date());
		venda.setCliente(cliente);

		return venda;
	}

}
