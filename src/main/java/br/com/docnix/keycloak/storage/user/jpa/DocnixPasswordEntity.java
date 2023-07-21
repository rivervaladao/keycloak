package br.com.docnix.keycloak.storage.user.jpa;

import jakarta.persistence.*;

@Table(name = "SGM_SENHA")
@Entity
public class DocnixPasswordEntity {
    @Id
    @Column(name = "id_senha")
    private Long id;
    @Column(name = "valor",nullable = false,length = 60)
    private String password;
    public Long getId(){
        return this.getId();
    }
    public void setId(Long id){
        this.id = id;
    }
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
