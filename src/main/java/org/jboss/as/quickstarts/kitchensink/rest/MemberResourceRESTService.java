/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.rest;

import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the members table.
 */
@RestController
public class MemberResourceRESTService {
    private final Logger log;
    private final MemberRepository repository;
    private final MemberRegistration registration;

    @Autowired
    public MemberResourceRESTService(MemberRepository repository, MemberRegistration registration) {
        this.log = Logger.getLogger(MemberResourceRESTService.class.getName());
        this.repository = repository;
        this.registration = registration;
    }

    @GetMapping({"/api/members"})
    @ResponseBody
    public List<Member> listAllMembers() {
        return repository.findAll();
    }

    @GetMapping("/api/members/{id:[0-9]+}")
    @ResponseBody
    public Member lookupMemberById(@PathVariable("id") long id) {
        Member member = repository.findById(BigInteger.valueOf(id));
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }
        return member;
    }

    @DeleteMapping("/api/members/{id:[0-9]+}")
    public void deleteMemberById(@PathVariable("id") long id) {
        Member member = repository.findById(BigInteger.valueOf(id));
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }
        repository.deleteMemberById(BigInteger.valueOf(id));
    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */
    @PostMapping("/api/members")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Member createMember(@RequestBody Member member) {

        try {
            // Validates member using bean validation
//            validateMember(member);
            registration.register(member);
//        }
//        catch (ConstraintViolationException ce) {
//            // Handle bean validation issues
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ce.getConstraintViolations());
////            builder = createViolationResponse(ce.getConstraintViolations());
//        } catch (ValidationException e) {
//            // Handle the unique constrain violation
//            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use by another member");
////            Map<String, String> responseObj = new HashMap<>();
////            responseObj.put("email", "Email taken");
////            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            // Handle generic exceptions
//            Map<String, String> responseObj = new HashMap<>();
//            responseObj.put("error", e.getMessage());
//            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return member;
    }

    /**
     * <p>
     * Validates the given Member variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     *
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
//    private void validateMember(Member member) throws ConstraintViolationException, ValidationException {
//        // Create a bean validator and check for issues.
//        Set<ConstraintViolation<Member>> violations = validator.(member);
//
//        if (!violations.isEmpty()) {
//            throw new ConstraintViolationException(new HashSet<>(violations));
//        }
//
//        // Check the uniqueness of the email address
//        if (emailAlreadyExists(member.getEmail())) {
//            throw new ValidationException("Unique Email Violation");
//        }
//    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     *
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
//    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
//        log.fine("Validation completed. violations found: " + violations.size());
//
//        Map<String, String> responseObj = new HashMap<>();
//
//        for (ConstraintViolation<?> violation : violations) {
//            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
//        }
//
//        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
//    }

    /**
     * Checks if a member with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
     *
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
    public boolean emailAlreadyExists(String email) {
        Member member = null;
        try {
            member = repository.findByEmail(email);
        } catch (Exception e) {
            // ignore
        }
        return member != null;
    }
}
