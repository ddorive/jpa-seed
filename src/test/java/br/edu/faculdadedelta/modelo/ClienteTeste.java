package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class ClienteTeste {
			private static final String CPF_PADRAO = "000.111.222-10";
			private EntityManager em;
			
			
			
			@SuppressWarnings("unchecked")
			@Test
			public void deveConsultarCpf(){
				
				deveSalvarCliente();
				
				Query query = em.createQuery("SELECT c.cpf FROM Cliente c WHERE" + "c.nome like :nome");
				
				query.setParameter("nome",	"%Atila%");
				
				List<String> cpfs = query.getResultList();
				assertFalse(cpfs.isEmpty());
			
				
			}
			
			
			@Test
			public void deveSalvarCliente() {
				Cliente cliente = new Cliente();
				cliente.setNome("Atila");
				cliente.setCpf(CPF_PADRAO);

				assertTrue("entidade nao temID ainda", cliente.isTransient());

				em.getTransaction().begin();
				em.persist(cliente);
				em.getTransaction().commit();

				assertFalse("entidade agora temID ", cliente.isTransient());
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

}
