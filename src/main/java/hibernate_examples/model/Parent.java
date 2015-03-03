package hibernate_examples.model;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableSet;
import static javax.persistence.CascadeType.ALL;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "NAME"))
public class Parent implements hibernate_examples.hibernate.Entity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = ALL, orphanRemoval = true)
    private Set<Child> children;

    public Parent(String name) {
        this.id = UUID.randomUUID();
        this.name = checkNotNull(name);
        this.children = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Child> getChildren() {
        return unmodifiableSet(children);
    }

    public Child addChild(String name) {
        final Child child = new Child(this, name);
        if (children.add(child))
            return child;
        else
            return children.stream().filter(child::equals).findFirst().get();
    }

    public Child replaceChild(String initialChildName, String newChildName) {
        removeChild(initialChildName);
        return addChild(newChildName);
    }

    public void removeChild(String initialChildName) {
        children.removeIf(child -> child.getName().equals(initialChildName));
    }

    public Child replaceAllChildrenWith(String name) {
        children.clear();
        return addChild(name);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;

        final Parent child = (Parent) o;

        return getName().equals(child.getName());
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final String toString() {
        return "Parent{" +
                "name='" + getName() + '\'' +
                ", children=" + getChildren() +
                '}';
    }

    // Solely for Hibernate
    protected Parent() {
    }
}
