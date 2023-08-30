package org.upsmf.grievance.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.gax.rpc.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.upsmf.grievance.dto.CreateUserDto;
import org.upsmf.grievance.dto.UserCredentials;
import org.upsmf.grievance.dto.UserDto;
import org.upsmf.grievance.dto.UserResponseDto;
import org.upsmf.grievance.enums.Department;
import org.upsmf.grievance.exception.runtime.InvalidRequestException;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.model.UserRole;
import org.upsmf.grievance.repository.DepartmentRepository;
import org.upsmf.grievance.repository.RoleRepository;
import org.upsmf.grievance.repository.UserRepository;
import org.upsmf.grievance.repository.UserRoleRepository;
import org.upsmf.grievance.service.IntegrationService;

import java.util.*;

@Service
@Slf4j
public class IntegrationServiceImpl implements IntegrationService {

    public static final String ROLE = "Role";
    @Autowired
    private  RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Value("${api.user.createUrl}")
    private String createUserUrl;

    @Value("${api.user.updateUrl}")
    private String updateUserUrl;
    @Value("${api.user.searchUrl}")
    private String apiUrl;
    @Value("${api.user.listUrl}")
    private String listUserUrl;
    @Value("${api.user.activeUserUrl}")
    private String activeUserUrl;

    @Value("${api.user.deactivateUserUrl}")
    private String deactivateUserUrl;
    @Value("${api.user.loginUserUrl}")
    private String loginUserUrl;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public ResponseEntity<User> createUser(CreateUserDto user) throws Exception {
        // check for department
        String module = user.getAttributes().get("module");
        if(module != null) {
            user.getAttributes().put("module", module);
        } else {
            user.getAttributes().put("module", "grievance");
        }
        String departmentId = user.getAttributes().get("departmentName");
        List<Department> departmentList = new ArrayList<>();
        if(departmentId != null) {
            departmentList = Department.getById(Integer.valueOf(departmentId));
            if(departmentList != null && !departmentList.isEmpty()) {
                user.getAttributes().put("departmentName", departmentList.get(0).name());
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNodeObject = mapper.convertValue(user, JsonNode.class);
        JsonNode root = mapper.createObjectNode();
        ((ObjectNode) root).put("request", jsonNodeObject);
        log.info("Create user Request - {}", root);
        ResponseEntity response = restTemplate.exchange(createUserUrl, HttpMethod.POST,
                new HttpEntity<>(root, headers), String.class);
        log.info("Create user Response - {}", response);
        if (response.getStatusCode() == HttpStatus.OK) {
            String userContent = response.getBody().toString();


            ObjectNode requestNode = mapper.createObjectNode();
            requestNode.put("userName", userContent);
            JsonNode payload = requestNode;
            JsonNode payloadRoot = mapper.createObjectNode();
            ((ObjectNode) payloadRoot).put("request", payload);
            ResponseEntity<String> getUsersResponse = getUsers(payloadRoot);

            if (getUsersResponse.getStatusCode() == HttpStatus.OK) {
                String getUsersResponseBody = getUsersResponse.getBody();
                JsonNode getUsersJsonNode = mapper.readTree(getUsersResponseBody);

                if(getUsersJsonNode.size() > 0) {
                    JsonNode userContentData = getUsersJsonNode;
                    User newUser = createUserWithApiResponse(userContentData);
                    User savedUser = userRepository.save(newUser);
                    // create user role mapping
                    createUserRoleMapping(user, savedUser);
                    // create user department mapping
                    if(savedUser != null && savedUser.getId() > 0 && departmentList != null && !departmentList.isEmpty()) {
                        org.upsmf.grievance.model.Department departmentMap = org.upsmf.grievance.model.Department.builder().departmentName(departmentList.get(0).name()).userId(savedUser.getId()).build();
                        org.upsmf.grievance.model.Department userDepartment = departmentRepository.save(departmentMap);
                        List<org.upsmf.grievance.model.Department> departments = new ArrayList<>();
                        departments.add(userDepartment);
                        savedUser.setDepartment(departments);
                    }
                    return new ResponseEntity<>(savedUser, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            else {
                // Handle error cases here
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void createUserRoleMapping(CreateUserDto user, User savedUser) {
        if(savedUser != null && savedUser.getId() > 0) {
            String role = user.getAttributes().get(ROLE);
            if(role != null && !role.isBlank()) {
                Role roleDetails = roleRepository.findByName(role);
                if(roleDetails != null) {
                    UserRole userRole = UserRole.builder().userId(savedUser.getId()).roleId(roleDetails.getId()).build();
                    userRoleRepository.save(userRole);
                }
            }
        }
    }


    @Override
    public ResponseEntity<String> updateUser(UserDto userDto) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.createObjectNode();
        JsonNode request= mapper.createObjectNode();
        //((ObjectNode) request).put("userId",userDto.getUserId());
        ((ObjectNode) request).put("password",userDto.getPassword());
        ((ObjectNode) root).put("request", request);
        //TODO need to create dynamically create body
            ResponseEntity<String> response = restTemplate.exchange(
                    updateUserUrl, HttpMethod.PUT,
                    new HttpEntity<>(root, headers), String.class
            );
            return response;

    }

    @Override
    public ResponseEntity<String> getUsers(JsonNode payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<UserResponseDto> childNodes = new ArrayList<>();
        Pageable pageable = PageRequest.of(payload.get("page").asInt(),payload.get("size").asInt(), Sort.by(Sort.Direction.DESC, "id"));
        Page<User> users = userRepository.findAll(pageable);
        if(users.hasContent()) {
            for (User user : users.getContent()){
                childNodes.add(createUserResponse(user));
            }
        }
        JsonNode userResponse = mapper.createObjectNode();
        ((ObjectNode) userResponse).put("count", users.getTotalElements());
        ArrayNode nodes = mapper.valueToTree(childNodes);
        ((ObjectNode) userResponse).put("result", nodes);
        return ResponseEntity.ok(mapper.writeValueAsString(userResponse));
    }

    @Override
    public void assignRole(Long userId, Long roleId) throws NotFoundException {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                userRepository.save(user.get());
            }
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Role not found", e);
        }
    }


    private User createUserWithApiResponse(JsonNode userContent)throws Exception{
        String[] rolesArray = new String[0];
        String[] departmentArray = new String[0];

        JsonNode rolesNode = userContent.path("attributes").path(ROLE);
        JsonNode departmentNode = userContent.path("attributes").path("departmentName");
        if (rolesNode.isArray()) {
            rolesArray = new String[rolesNode.size()];
            for (int i = 0; i < rolesNode.size(); i++) {
                rolesArray[i] = rolesNode.get(i).asText();
            }
        }

        if(departmentNode.isArray() && !departmentNode.isEmpty()) {
            departmentArray = new String[rolesNode.size()];
            for (int i = 0; i < departmentNode.size(); i++) {
                departmentArray[i] = departmentNode.get(i).asText();
            }
        }

        return User.builder()
                .keycloakId(userContent.path("id").asText())
                .firstName(userContent.path("firstName").asText())
                .lastname(userContent.path("lastName").asText())
                .username(userContent.path("username").asText())
                .phoneNumber(userContent.path("attributes").path("phoneNumber").get(0).asText())
                .email(userContent.path("email").asText())
                .emailVerified(userContent.path("emailVerified").asBoolean())
                .status(userContent.path("enabled").asInt())
                .roles(rolesArray)
                .build();

    }

    @Override
    public ResponseEntity<String> getUsersFromKeycloak(JsonNode payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<UserResponseDto> childNodes = new ArrayList<>();
        int i=0;
        ResponseEntity<String> response = restTemplate.exchange(
                listUserUrl,
                HttpMethod.POST,
                new HttpEntity<>(payload),
                String.class
        );
        if(response.getStatusCode() == HttpStatus.OK) {
            String getUsersResponseBody = response.getBody();
            ArrayNode getUsersJsonNode = (ArrayNode) mapper.readTree(getUsersResponseBody);
            if(getUsersJsonNode.size() > 0) {
               for(JsonNode node : getUsersJsonNode) {
                   if(node.path("attributes") != null && !node.path("attributes").isEmpty()
                           && node.path("attributes").path("module") != null && !node.path("attributes").path("module").isEmpty()
                           && node.get("attributes").path("module").get(0).textValue().equalsIgnoreCase("grievance")) {
                       log.info("Grievance user node found || {} -- {}",i++, node);
                       User user = createUserWithApiResponse(node);
                       childNodes.add(createUserResponse(user));
                   }
               }
            }
        }
        JsonNode userResponse = mapper.createObjectNode();
        ((ObjectNode) userResponse).put("count", i);
        ArrayNode nodes = mapper.valueToTree(childNodes);
        ((ObjectNode) userResponse).put("result", nodes);
        return ResponseEntity.ok(mapper.writeValueAsString(userResponse));
    }

    @Override
    public ResponseEntity<String> searchUsers(JsonNode payload) throws Exception {

        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(payload),
                String.class
        );
        return response;
    }

    @Override
    public ResponseEntity<UserResponseDto> getUserById(long id) throws RuntimeException {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            User userDetails = user.get();
        return new ResponseEntity<>(createUserResponse(userDetails), HttpStatus.OK);
        }
        throw new RuntimeException("User details not found.");
    }

    @Override
    public ResponseEntity<User> activateUser(JsonNode payload) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(
                activeUserUrl, HttpMethod.POST,
                new HttpEntity<>(payload), String.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            String getUsersResponseBody = response.getBody();
            JsonNode getUsersJsonNode = mapper.readTree(getUsersResponseBody);

            JsonNode userContentData = getUsersJsonNode.path("result").path("response").path("content").get(0);

            User newUser = createUserWithApiResponse(userContentData);
            User savedUser = userRepository.save(newUser);

            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        }
        else {
            // Handle error cases here
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<User> deactivateUser(JsonNode payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ResponseEntity<String> response = restTemplate.exchange(
                deactivateUserUrl, HttpMethod.POST,
                new HttpEntity<>(payload), String.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            String getUsersResponseBody = response.getBody();
            JsonNode getUsersJsonNode = mapper.readTree(getUsersResponseBody);

            JsonNode userContentData = getUsersJsonNode.path("result").path("response").path("content").get(0);

            User newUser = createUserWithApiResponse(userContentData);
            User savedUser = userRepository.save(newUser);

            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        }
        else {
            // Handle error cases here
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> login(JsonNode body) {
        ResponseEntity<String> response = restTemplate.exchange(
                loginUserUrl, HttpMethod.POST,
                new HttpEntity<>(body), String.class
        );
        return response;
    }


    /**
     *  API to change password
     *  sample body -
     *  {
     *     "credentials": [
     *       {
     *         "type": "password",
     *         "value": "ka09eF$299",
     *         "temporary": "false"
     *       }
     *     ]
     *     }
     * }
     * @param userCredentials
     */
    public void changePassword(UserCredentials userCredentials) {
        // validate Request
        validateChangePasswordRequest(userCredentials);
        ObjectMapper mapper = new ObjectMapper();

    }

    private void validateChangePasswordRequest(UserCredentials userCredentials) {
        if(userCredentials == null) {
            throw new InvalidRequestException("Invalid Request");
        }

    }

    private UserResponseDto createUserResponse(User body) {
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("Role", Arrays.asList(body.getRoles()));
        List<String> department = new ArrayList<>();
        if(body.getDepartment() != null && !body.getDepartment().isEmpty()) {
            for(org.upsmf.grievance.model.Department depart : body.getDepartment()) {
                department.add(depart.getDepartmentName());
            }
        }
        attributes.put("departmentName", department);
        attributes.put("phoneNumber", Arrays.asList(body.getPhoneNumber()));
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .email(body.getEmail())
                .emailVerified(body.isEmailVerified())
                .enabled(Boolean.valueOf(String.valueOf(body.getStatus())))
                .firstName(body.getFirstName())
                .lastName(body.getLastname())
                .id(body.getId())
                .keycloakId(body.getKeycloakId())
                .username(body.getUsername())
                .attributes(attributes)
                .build();
        return userResponseDto;
    }


}
