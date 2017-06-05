package net.test.dto;

import java.io.Serializable;

/**
 * @author Zbynek Vavros (zbynek.vavros@i.cz)
 */
public class PersonDTO implements Serializable {

    private Long id;
    private String name;
    private String surname;

    public PersonDTO(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PersonDTO personDTO = (PersonDTO) o;

        if (id != null ? !id.equals(personDTO.id) : personDTO.id != null)
            return false;
        if (name != null ? !name.equals(personDTO.name) : personDTO.name != null)
            return false;
        return surname != null ? surname.equals(personDTO.surname) : personDTO.surname == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "net.test.dto.PersonDTO{" + "id=" + id + ", name='" + name + '\'' + ", surname='" + surname + '\'' + '}';
    }
}
