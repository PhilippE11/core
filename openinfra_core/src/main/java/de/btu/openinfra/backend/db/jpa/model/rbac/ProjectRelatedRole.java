package de.btu.openinfra.backend.db.jpa.model.rbac;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.btu.openinfra.backend.db.jpa.model.OpenInfraModelObject;


/**
 * The persistent class for the project_related_roles database table.
 * 
 */
@Entity
@Table(name="project_related_roles")
@NamedQuery(name="ProjectRelatedRole.findAll", query="SELECT p FROM ProjectRelatedRole p")
public class ProjectRelatedRole extends OpenInfraModelObject
	implements Serializable {
	private static final long serialVersionUID = 1L;

	private String description;

	private String name;

	//bi-directional many-to-one association to SubjectProject
	@OneToMany(mappedBy="projectRelatedRoleBean")
	private List<SubjectProject> subjectProjects;

	public ProjectRelatedRole() {
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SubjectProject> getSubjectProjects() {
		return this.subjectProjects;
	}

	public void setSubjectProjects(List<SubjectProject> subjectProjects) {
		this.subjectProjects = subjectProjects;
	}

	public SubjectProject addSubjectProject(SubjectProject subjectProject) {
		getSubjectProjects().add(subjectProject);
		subjectProject.setProjectRelatedRoleBean(this);

		return subjectProject;
	}

	public SubjectProject removeSubjectProject(SubjectProject subjectProject) {
		getSubjectProjects().remove(subjectProject);
		subjectProject.setProjectRelatedRoleBean(null);

		return subjectProject;
	}

}