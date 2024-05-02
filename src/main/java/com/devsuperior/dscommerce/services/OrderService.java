package com.devsuperior.dscommerce.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.dto.OrderItemDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.OrderStatus;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;

@Service
public class OrderService {

	 @Autowired
	 private OrderRepository repository;
	 
	 @Autowired
	 private ProductRepository productRepository;
	 
	 @Autowired
	 private UserService userService;
	 
	 @Autowired
	 private OrderItemRepository orderItemRepository;
	 
	 
	 @Transactional(readOnly = true)
	    public OrderDTO findById(Long id) {
	        Order order = repository.findById(id).orElseThrow(
	                () -> new ResourceNotFoundException("Recurso não encontrado"));
	        return new OrderDTO(order);
	    }

	@Transactional(readOnly = true)
	public OrderDTO insert( OrderDTO dto) {
		Order order = new Order();
		
		order.setMoment(Instant.now());
		order.setStatus(OrderStatus.WAITING_PAYMENT);
		
		User user = userService.authenticated();
		order.setClient(user);
		//varrer a requisição do json do postman(que é o dto)
		for(OrderItemDTO itemDTO : dto.getItems()) {
			//instancia o produto pela função getReferenceById passando o id que vem do postman
			Product product = productRepository.getReferenceById(itemDTO.getProductId());
			//os itens do pedido faz referencia ao produto e peido n-n com mais requisitos
			OrderItem item = new OrderItem(order, product, itemDTO.getQuantity(), product.getPrice());
			//agora associa os itens do pedido ao pedido
			order.getItems().add(item);
			
		}
		
		//agora vamos salvar o estrutura em memória montada acima
		repository.save(order);
		//existe uma associação de classe e então temos que salvar a tabela do n-n. Vamos criar o repository da 
		orderItemRepository.saveAll(order.getItems());
		//agora retorna o oderDTO atualizado
		return new OrderDTO(order);
		
	}
}
