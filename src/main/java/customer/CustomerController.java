package customer;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/customers")
public class CustomerController {
    private CustomerRepository customerRepository;

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    @RequestMapping(method = RequestMethod.GET)
    public Collection<Customer> customers(){
        return customerRepository.findAllByOrderByIdDesc();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Customer getCustomerById(@PathVariable Long id){
        return customerRepository.getOne(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> add(@RequestBody Customer customerToAdd){

        Customer result = customerRepository.save(new Customer(customerToAdd.getName(),
                customerToAdd.getEmail()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getId()).toUri());
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<?> editCustomer(@PathVariable Long id, @RequestBody Customer editedCustomer){

        Customer record = customerRepository.getOne(id);

        if(editedCustomer.getName() != null){
            record.setName(editedCustomer.getName());
        }
        if(editedCustomer.getEmail() != null){
            record.setEmail(editedCustomer.getEmail());
        }

        Customer result = customerRepository.save(record);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getId()).toUri());
        return new ResponseEntity<>(result, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id){
        customerRepository.deleteById(id);

        String response = String.format("Successfully deleted customer with ID %d", id);
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);

    }
}