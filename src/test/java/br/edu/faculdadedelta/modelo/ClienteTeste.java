package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class ClienteTeste {
	private static final String CPF_PADRAO = "000.111.222-10";
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarCpf() {

		deveSalvarCliente();
		String filtro = "Barros";

		Query query = em.createQuery("SELECT c.cpf FROM Cliente c WHERE " + "c.nome LIKE :nome");

		query.setParameter("nome", "%" + filtro + "%");

		List<String> cpfs = query.getResultList();
		assertFalse(cpfs.isEmpty());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void deveConsultarClienteComIdNome() {
		deveSalvarCliente();

		Query query = em.createQuery("SELECT new Cliente(c.id, c.nome) FROM Cliente c "
				                                                      + "WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);
		List<Cliente> clientes = query.getResultList();
		assertFalse("verifica se há registro na lista", clientes.isEmpty());
		
		
		//outro metodo para busca
		clientes.forEach(cliente -> {
			assertNull("nao deve ter CPF", cliente.getCpf());
		});

		/**for (Cliente cliente : clientes) {
			*assertNull("verifica que o cpf deve ser null", cliente.getCpf());
*
			*cliente.setCpf(CPF_PADRAO);
		*}
*/
	
	}

	@SuppressWarnings("unchecked")
	@Test

	public void deveConsultarIdNome() {
		deveSalvarCliente();

		Query query = em.createQuery("SELECT c.id, c.nome FROM Cliente c WHERE c.cpf = :cpf");
		query.setParameter("cpf", CPF_PADRAO);

		List<Object[]> resultado = query.getResultList();

		assertFalse("Verifica se há registro na lista", resultado.isEmpty());

		for (Object[] linha : resultado) {
			assertTrue("Verifica que o primeiro item e o ID", linha[0] instanceof Long);
			assertTrue("Verifica que o segundo item e o nome", linha[1] instanceof String);

			Cliente cliente = new Cliente((Long) linha[0], (String) linha[1]);
			assertNotNull(cliente);

		}

	}

	@Test
	public void deveVerificarExistenciaCliente(){
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT COUNT(c.id) FROM Cliente c WHERE c.cpf =:cpf");
		query.setParameter("cpf", CPF_PADRAO);
		
		Long qtdResultados = (Long) query.getSingleResult();
		
		assertTrue("Verifica se ha registros na lista", qtdResultados >0L);
	}
	
	@Test(expected = NonUniqueResultException.class)
	public void naoDeveFuncionarSingleResultComMuitosRegistros(){
		deveSalvarCliente();
		deveSalvarCliente();
		
		//Query query = em.createQuery("SELECT c.id FROM Cliente c WHERE c.cpf =:cpf" );
		
		Query query = em.createQuery("SELECT c.id FROM Cliente c" );
		
		//query.setParameter("cpf", CPF_PADRAO);

		query.getSingleResult();
		
		fail("Metodo getSingleResultdeve dispara exception NonUniqueResultException");
		
	}
	
	@Test(expected = NoResultException.class)
	public void naoDeveFuncionarSingleResultComNenhumRegistros(){
		deveSalvarCliente();
		deveSalvarCliente();
		
		Query query = em.createQuery("SELECT c.id FROM Cliente c WHERE c.cpf =:cpf" );
		
		query.setParameter("cpf", "000.111.222-33");
		
		query.getSingleResult();
		
		fail("Metodo getSingleResultdeve dispara exception NonResultException ");
		
	}
	
	@Test
	public void deveAcessarAtributoLazy(){
		deveSalvarCliente();
		
		Cliente cliente = em.find(Cliente.class, 1L);
		
		assertNotNull("Verifica se encontrou um registro", cliente);
		assertNotNull("lista Lazy nao deve ser null", cliente.getCompras() );
		
	}
	
	@Test(expected = LazyInitializationException.class)
	public void naoDeveAcessarAtributoLazyForEscopoEntityManager(){
		deveSalvarCliente();
		
		Cliente cliente = em.find(Cliente.class, 1L);
		
		assertNotNull("Verifica se encontrou um registro", cliente);
		
		em.detach(cliente);
		
		//em.clear(); --> limpa toda istancia
		
		cliente.getCompras().size();
		
		fail("Deve disparar LazyInitializationException ao acesar"
		                       + " atributo Lazy de um objeto fora de escopo do EntityManager");
		
	}
	
	
	@Test
	public void deveSalvarCliente() {
		Cliente cliente = new Cliente();
		cliente.setNome("Atila Barros");
		cliente.setCpf(CPF_PADRAO);

		assertTrue("Entidade nao temID ainda", cliente.isTransient());

		em.getTransaction().begin();
		em.persist(cliente);
		em.getTransaction().commit();

		assertFalse("Entidade agora temID ", cliente.isTransient());
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

		Query query = entityManager.createQuery("DELETE FROM Cliente c");
		int registroExcuidos = query.executeUpdate();

		entityManager.getTransaction().commit();

		assertTrue("deve ter excluido registros", registroExcuidos > 0);

	}

}
