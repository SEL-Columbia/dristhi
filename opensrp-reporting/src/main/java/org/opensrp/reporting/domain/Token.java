package org.opensrp.reporting.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "token")
@NamedQuery(name = Token.FIND_TOKEN_BY_NAME,
        query = "select t from Token t where t.name = :name")
public class Token {

    public static final String FIND_TOKEN_BY_NAME = "find.token.by.name";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    private Token() {
    }

    public Token(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String value() {
        return value;
    }

    public Token withValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, new String[]{"id"});
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[]{"id"});
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
