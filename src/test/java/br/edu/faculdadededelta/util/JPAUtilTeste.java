package br.edu.faculdadededelta.util;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

import static org.junit.Assert.*;

public class JPAUtilTeste {
	
	private EntityManager em;
	
	@Test
	
	public void deveTerInstanciadoEntityManager(){
		assertNotNull("Deve instanciado o entity manager", em);
	}
	
	@Test
	
	public void deveFechaEntityManager(){
		em.close();
		assertFalse("EntityMangert deve fechar" , em.isOpen());

	}
	
	@Test
	
	public void deveAbrirUmaTrancao(){
	
		assertFalse("Trasacao deve estar fechada" , em.getTransaction().isActive());
		
		em.getTransaction().begin();
		assertTrue("Transacao deve estar aberta" , em.getTransaction().isActive());
		
	}
	
	
	@Before
	public void instanciarEntityManager(){
		em = JPAUtil.INSTANCE.getEntityManager();
		
		
	}
	
	@After
	public void fecharEntityManager(){
		if(em.isOpen()){
			em.close();
		}
		
	}

}
