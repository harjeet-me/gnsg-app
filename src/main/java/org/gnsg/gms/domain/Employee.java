package org.gnsg.gms.domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * The Employee entity.
 */
@ApiModel(description = "The Employee entity.")
@Entity
@Table(name = "employee")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    /**
     * The firstname attribute.
     */
    @ApiModelProperty(value = "The firstname attribute.")
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "seva_start_date")
    private Instant sevaStartDate;

    @Column(name = "seva_end_date")
    private Instant sevaEndDate;

    @Column(name = "is_valid")
    private Boolean isValid;

    @OneToMany(mappedBy = "employee")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Event> pathiSinghs = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Event> ragiSinghs = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Event> bookingBies = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Event> updatedBies = new HashSet<>();

    @OneToMany(mappedBy = "employee")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<File> files = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public Employee firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Employee lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public Employee email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Employee phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getSevaStartDate() {
        return sevaStartDate;
    }

    public Employee sevaStartDate(Instant sevaStartDate) {
        this.sevaStartDate = sevaStartDate;
        return this;
    }

    public void setSevaStartDate(Instant sevaStartDate) {
        this.sevaStartDate = sevaStartDate;
    }

    public Instant getSevaEndDate() {
        return sevaEndDate;
    }

    public Employee sevaEndDate(Instant sevaEndDate) {
        this.sevaEndDate = sevaEndDate;
        return this;
    }

    public void setSevaEndDate(Instant sevaEndDate) {
        this.sevaEndDate = sevaEndDate;
    }

    public Boolean isIsValid() {
        return isValid;
    }

    public Employee isValid(Boolean isValid) {
        this.isValid = isValid;
        return this;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Set<Event> getPathiSinghs() {
        return pathiSinghs;
    }

    public Employee pathiSinghs(Set<Event> events) {
        this.pathiSinghs = events;
        return this;
    }

    public Employee addPathiSingh(Event event) {
        this.pathiSinghs.add(event);
        event.setEmployee(this);
        return this;
    }

    public Employee removePathiSingh(Event event) {
        this.pathiSinghs.remove(event);
        event.setEmployee(null);
        return this;
    }

    public void setPathiSinghs(Set<Event> events) {
        this.pathiSinghs = events;
    }

    public Set<Event> getRagiSinghs() {
        return ragiSinghs;
    }

    public Employee ragiSinghs(Set<Event> events) {
        this.ragiSinghs = events;
        return this;
    }

    public Employee addRagiSingh(Event event) {
        this.ragiSinghs.add(event);
        event.setEmployee(this);
        return this;
    }

    public Employee removeRagiSingh(Event event) {
        this.ragiSinghs.remove(event);
        event.setEmployee(null);
        return this;
    }

    public void setRagiSinghs(Set<Event> events) {
        this.ragiSinghs = events;
    }

    public Set<Event> getBookingBies() {
        return bookingBies;
    }

    public Employee bookingBies(Set<Event> events) {
        this.bookingBies = events;
        return this;
    }

    public Employee addBookingBy(Event event) {
        this.bookingBies.add(event);
        event.setEmployee(this);
        return this;
    }

    public Employee removeBookingBy(Event event) {
        this.bookingBies.remove(event);
        event.setEmployee(null);
        return this;
    }

    public void setBookingBies(Set<Event> events) {
        this.bookingBies = events;
    }

    public Set<Event> getUpdatedBies() {
        return updatedBies;
    }

    public Employee updatedBies(Set<Event> events) {
        this.updatedBies = events;
        return this;
    }

    public Employee addUpdatedBy(Event event) {
        this.updatedBies.add(event);
        event.setEmployee(this);
        return this;
    }

    public Employee removeUpdatedBy(Event event) {
        this.updatedBies.remove(event);
        event.setEmployee(null);
        return this;
    }

    public void setUpdatedBies(Set<Event> events) {
        this.updatedBies = events;
    }

    public Set<File> getFiles() {
        return files;
    }

    public Employee files(Set<File> files) {
        this.files = files;
        return this;
    }

    public Employee addFile(File file) {
        this.files.add(file);
        file.setEmployee(this);
        return this;
    }

    public Employee removeFile(File file) {
        this.files.remove(file);
        file.setEmployee(null);
        return this;
    }

    public void setFiles(Set<File> files) {
        this.files = files;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee)) {
            return false;
        }
        return id != null && id.equals(((Employee) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Employee{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", sevaStartDate='" + getSevaStartDate() + "'" +
            ", sevaEndDate='" + getSevaEndDate() + "'" +
            ", isValid='" + isIsValid() + "'" +
            "}";
    }
}
