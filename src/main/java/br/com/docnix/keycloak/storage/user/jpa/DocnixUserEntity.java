/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.docnix.keycloak.storage.user.jpa;

import jakarta.persistence.*;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@NamedQueries({
        @NamedQuery(name="getUserByUsername", query="select u from DocnixUserEntity u where u.username = :username"),
        @NamedQuery(name="getUserByEmail", query="select u from DocnixUserEntity u where u.email = :email"),
        @NamedQuery(name="getUserCount", query="select count(u) from DocnixUserEntity u"),
        @NamedQuery(name="getAllUsers", query="select u from DocnixUserEntity u"),
        @NamedQuery(name="searchForUser", query="select u from DocnixUserEntity u where " +
                "lower(u.username) like :search  order by u.username"),

//        @NamedQuery(name="searchForUser", query="select u from UserEntity u where " +
//                "( lower(u.username) like :search or u.email like :search ) order by u.username"),
})
@Entity
@Table(name = "sgm_usuario")
public class DocnixUserEntity {
    @Id
    @Column(name = "id_usuario")
    private Long id;
    @Column(name = "login")
    private String username;
    @Column(name = "email")
    private String email;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_senha_atual", referencedColumnName = "id_senha")
    private DocnixPasswordEntity password;
    @Column(name = "telefone")
    private String phone;

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(String id) {
        this.id = Long.valueOf(id);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password.getPassword();
    }

    public void setPassword(String password) {
        this.password.setPassword(password);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
