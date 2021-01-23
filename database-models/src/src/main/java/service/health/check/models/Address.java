package service.health.check.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="addresses")
public class Address implements Serializable {

	private static final long serialVersionUID = -6789189734956800614L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID", unique = true, nullable = false)
	private Integer id;

	@Column(name = "host", length = 256, nullable = false)
	private String host;

	@Column(name = "port", length = 256, nullable = false)
	private String port;

	@Column(name = "last_healthy")
	private Timestamp lastHealthy;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Address that = (Address) obj;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Timestamp getLastHealthy() {
		return lastHealthy;
	}

	public void setLastHealthy(Timestamp lastHealthy) {
		this.lastHealthy = lastHealthy;
	}
}
