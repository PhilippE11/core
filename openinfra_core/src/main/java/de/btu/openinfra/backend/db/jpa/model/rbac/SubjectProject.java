package de.btu.openinfra.backend.db.jpa.model.rbac;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.btu.openinfra.backend.db.jpa.model.OpenInfraModelObject;


/**
 * The persistent class for the subject_projects database table.
 *
 */
@Entity
@Table(name="subject_projects")
@NamedQueries({
	@NamedQuery(name="SubjectProject.findAll",
			query="SELECT s FROM SubjectProject s ORDER BY s.id"),
	@NamedQuery(name="SubjectProject.count",
		query="SELECT COUNT(s) FROM SubjectProject s")
})
public class SubjectProject extends OpenInfraModelObject
	implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="project_id")
	private UUID projectId;

	//bi-directional many-to-one association to ProjectRelatedRole
	@ManyToOne
	@JoinColumn(name="project_related_role_id")
	private ProjectRelatedRole projectRelatedRoleBean;

	//bi-directional many-to-one association to Subject
	@ManyToOne
	@JoinColumn(name="subject_id")
	private Subject subjectBean;

	public SubjectProject() {
	}

	public UUID getProjectId() {
		return this.projectId;
	}

	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}

	public ProjectRelatedRole getProjectRelatedRoleBean() {
		return this.projectRelatedRoleBean;
	}

	public void setProjectRelatedRoleBean(ProjectRelatedRole projectRelatedRoleBean) {
		this.projectRelatedRoleBean = projectRelatedRoleBean;
	}

	public Subject getSubjectBean() {
		return this.subjectBean;
	}

	public void setSubjectBean(Subject subjectBean) {
		this.subjectBean = subjectBean;
	}

}