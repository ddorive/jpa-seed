package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class VendaTeste {

	private EntityManager em;
	private static final String CPF_PADRAO = "000.111.222-10";

	@Test
	public void deveSavarVendaComRelacionamentoEmCascata() {
		Venda venda = criarVenda();

		venda.getProdutos().add(criarProduto("Notebook", "Dell"));
		venda.getProdutos().add(criarProduto("Mouse", "Razer"));

		assertTrue("Nao deve ter ID definido", venda.isTransient());

		em.getTransaction().begin();
		em.persist(venda);
		em.getTransaction().commit();

		assertFalse("Deve ter ID definido", venda.isTransient());
		assertFalse("Deve ter ID definido", venda.getCliente().isTransient());

		for (Produto produto : venda.getProdutos()) {
			assertFalse("Deve ter ID definido", venda.isTransient());
		}

	}

	@Test(expected = IllegalStateException.class)
	public void naoDeveFazerMergeEmObjetosTransient() {
		Venda venda = criarVenda();

		venda.getProdutos().add(criarProduto("Notebook", "Dell"));
		venda.getProdutos().add(criarProduto("Mouse", "Razer"));

		assertTrue("Nao deve ter ID definido", venda.isTransient());

		em.getTransaction().begin();
		em.merge(venda);
		em.getTransaction().commit();

		fail("Não deveria ter salvo (merge) uma venda nova com relacionamento trasient");

	}

	@Test
	public void deveConsultarQdtDeProdutosVendidos() {
		Venda venda = CriarVenda("000.111.222-33");

		for (int i = 0; i < 10; i++) {
			venda.getProdutos().add(criarProduto("Produto" + i, "Marca" + i));
		}
		em.getTransaction().begin();
		em.persist(venda);
		em.getTransaction().commit();

		assertFalse("Deve ter persistido a venda", venda.isTransient());

		int qtdItens = venda.getProdutos().size();

		//assertTrue("lista de produtos deve ter itens", qtdProdutoAdicionados > 0);
		
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(p.id)" );
		hql.append(" FRON Venda v" );
		hql.append("INNER JOIN v.produtos p" );
		hql.append("INNER JOIN v.cliente c" );
		hql.append("WHERE c.cpf = :cpf");
		
		Query query = em.createQuery(hql.toString());
		query.setParameter("cpf", "000.111.222-33");
		Long qtdRegistro = (Long) query.getSingleResult();
		
		assertTrue(qtdItens == qtdRegistro.intValue());
		
		

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

	@AfterClass
	public static void deveLimparBaseTeste() {
		EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();

		entityManager.getTransaction().begin();

		Query query = entityManager.createQuery("DELETE FROM Venda v");
		int registroExcuidos = query.executeUpdate();

		entityManager.getTransaction().commit();

		assertTrue("deve ter excluido registros", registroExcuidos > 0);

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
