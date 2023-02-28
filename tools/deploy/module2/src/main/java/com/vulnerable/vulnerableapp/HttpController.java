package com.vulnerable.vulnerableapp;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.impl.TextCodec;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

@RestController
public class HttpController {

  public static final String[] SECRETS = {
      "victory", "business", "available", "shipping", "washington"
  };

  public static final String JWT_SECRET = TextCodec.BASE64.encode(SECRETS[new Random().nextInt(SECRETS.length)]);

  @Autowired
  private EmployeeRepository employeeRepository;

  @GetMapping("/employees")
  public List<Employee> getAllEmployees() {
    JDBCManager.executeQuery("Blah blah");
    return employeeRepository.findAll();
  }

  @GetMapping("/employees/{id}")
  public Employee getEmployeeById(@PathVariable(value = "id") Long employeeId) {
    return employeeRepository.findById(employeeId)
        .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));
  }

  @GetMapping("/employeesDynamic/{id}")
  public Employee getEmployeeByDId(@PathVariable(value = "id") Long employeeId) {
    return employeeRepository.findByFilterText(employeeId).get(0);

  }

  @GetMapping("/employeesByDynamicSql/{query}")
  public List<Employee> getEmployeeByDynamicSQL(@PathVariable(value = "query") String queryToExecute) {
    List<Employee> employees = JDBCManager.executeQuery(queryToExecute);
    return employees;
  }

  @PostMapping("/employees")
  public Employee createEmployee(@RequestBody Employee employee) {
    return employeeRepository.save(employee);
  }

  /*
   * This method is vulnerable to JWT token manipulation
   */
  @RequestMapping(path = "/JWT/secret/gettoken", produces = MediaType.TEXT_HTML_VALUE)
  @ResponseBody
  public String getSecretToken() {
    return Jwts.builder()
        .setIssuer("test Corp")
        .setAudience("testvuln.org")
        .setIssuedAt(Calendar.getInstance().getTime())
        .setExpiration(Date.from(Instant.now().plusSeconds(60)))
        .setSubject("jak@test.com")
        .claim("username", "John")
        .claim("Email", "johndoe@test.com")
        .claim("Role", new String[] { "Manager", "Project Administrator" })
        .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
        .compact();
  }

  /*
   * fake method to simulate clicking on the link in the email
   */
  private void fakeClickingLinkEmail(String host, String resetLink) {
    try {
      HttpHeaders httpHeaders = new HttpHeaders();
      HttpEntity httpEntity = new HttpEntity(httpHeaders);
      new RestTemplate()
          .exchange(
              String.format("http://%s/PasswordReset/reset/reset-password/%s", host, resetLink),
              HttpMethod.GET,
              httpEntity,
              Void.class);
    } catch (Exception e) {

    }
  }

  @GetMapping("/employees/redirect")
  public void fakeRedirect(String host, String resetLink) {
    fakeClickingLinkEmail(host, resetLink);
  }
}
