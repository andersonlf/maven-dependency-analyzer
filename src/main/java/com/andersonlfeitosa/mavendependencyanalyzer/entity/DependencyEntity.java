package com.andersonlfeitosa.mavendependencyanalyzer.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DEPENDENCY")
public class DependencyEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idDependency")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "idArtifact", referencedColumnName = "idArtifact")
	private ArtifactEntity artifact;

	@ManyToOne (cascade = CascadeType.PERSIST)
	@JoinColumn(name = "idArtifactDependentOn", referencedColumnName = "idArtifact")
	private ArtifactEntity dependency;

	@Column(nullable = true)
	private String scope;

	@Column(nullable = true)
	private String classifier;

	@Column(nullable = true)
	private String type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public ArtifactEntity getDependency() {
		return dependency;
	}

	public void setDependency(ArtifactEntity dependency) {
		this.dependency = dependency;
	}

	public ArtifactEntity getArtifact() {
		return artifact;
	}

	public void setArtifact(ArtifactEntity artifact) {
		this.artifact = artifact;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((artifact == null) ? 0 : artifact.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DependencyEntity other = (DependencyEntity) obj;
		if (artifact == null) {
			if (other.artifact != null)
				return false;
		} else if (!artifact.equals(other.artifact))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}