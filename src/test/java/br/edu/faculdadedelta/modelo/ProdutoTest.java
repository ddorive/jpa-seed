package br.edu.faculdadedelta.modelo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import br.edu.faculdadedelta.util.JPAUtil;

public class ProdutoTest {

	private EntityManager em;
	
		
	@Test
	public void deveExcluirProduto(){
		deveSalvarProduto();
		
		TypedQuery<Long> query = em.createQuery("SELECT MAX(p.id) FROM Produto p", Long.class);
		
		Long id = query.getSingleResult();
		
		em.getTransaction().begin();
		
		Produto produto = em.find(Produto.class, id);
		em.remove(produto);
		
		em.getTransaction().commit();
		Produto produtoExcluido = em.find(Produto.class, id);
		assertNull("naõ deve achar o produto excluid" , produtoExcluido);
		
	}
	
	
	@Test
	public void deveAterarProduto(){
		deveSalvarProduto();
		
		TypedQuery<Produto> query = em.createQuery("SELECT p FROM Produto p", Produto.class).setMaxResults(1);
		
		Produto produto = query.getSingleResult();
		assertNotNull("deve ter encontrado um produto", produto);
		
		Integer versao = produto.getVersion();
		
		em.getTransaction().begin();
		produto.setFabricante("HP");
		produto = em.merge(produto);
		em.getTransaction().commit();
		
		assertNotEquals("versao deve ser diferente", versao, produto.getVersion());
	}
	

	@Test
	public void devePesquisarProdutos() {
		for (int i = 0; i < 10; i++) {
			deveSalvarProduto();
		}
		TypedQuery<Produto> query = em.createQuery("SELECT p FROM Produto p", Produto.class);
		List<Produto> produtos = query.getResultList();

		assertFalse("deve ter itens da lista", produtos.isEmpty());
		assertTrue("deve ter pelomenos 10 itens", produtos.size() >= 10);

	}

	@Test
	public void deveSalvarProduto() {
		Produto produto = new Produto();
		produto.setNome("Notbook");
		produto.setFabricante("Dell");

		assertTrue("entidade nao temID ainda", produto.isTransient());

		em.getTransaction().begin();
		em.persist(produto);
		em.getTransaction().commit();

		assertFalse("entidade agora temID ", produto.isTransient());
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
public static void deveLimparBaseTeste(){
	EntityManager entityManager = JPAUtil.INSTANCE.getEntityManager();
	
	entityManager.getTransaction().begin();
	
	Query query = entityManager.createQuery("DELETE FROM Produto p");
	int registroExcuidos = query.executeUpdate();
	
	entityManager.getTransaction().commit();
	
	assertTrue("deve ter excluido registros",  registroExcuidos >0);
	
}
	
}
