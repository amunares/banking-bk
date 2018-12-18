package banking.customers.application;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.NamingConventions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import banking.accounts.application.dto.BankAccountDto;
import banking.accounts.domain.entity.BankAccount;
import banking.common.application.Notification;
import banking.customers.application.dto.CustomerDto;
import banking.customers.domain.entity.Customer;
import banking.customers.domain.repository.CustomerRepository;
import banking.users.application.dto.UserDto;
import banking.users.domain.entity.User;

@Service
public class CustomerApplicationService {
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	@Value("${maxPageSize}")
	private int maxPageSize;
    
	public CustomerDto create(CustomerDto customerDto) {
		Customer customer = mapper.map(customerDto, Customer.class);
		customer.setIsActive(true);
		customer = customerRepository.save(customer);
		customerDto = mapper.map(customer, CustomerDto.class);
        return customerDto;
    }
	
	public List<CustomerDto> get(int page, int pageSize) {
		Notification notification = this.getValidation(page, pageSize);
        if (notification.hasErrors()) {
            throw new IllegalArgumentException(notification.errorMessage());
        }
		List<Customer> customers = this.customerRepository.get(page, pageSize);
		List<CustomerDto> customersDto = mapper.map(customers, new TypeToken<List<CustomerDto>>() {}.getType());
        return customersDto;
    }
	
	
	public CustomerDto get(long customerId) {
		ModelMapper modelMapper = new ModelMapper();
		Customer customer = this.customerRepository.getById(customerId);
		CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
        return customerDto;
    }
	
	
	private Notification getValidation(int page, int pageSize) {
		Notification notification = new Notification();
		if (pageSize > maxPageSize) {
			notification.addError("Page size can not be greater than 100");
		}
		return notification;
	}
}
