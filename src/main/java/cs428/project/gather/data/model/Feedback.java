package cs428.project.gather.data.model;


import java.sql.Timestamp;

import javax.persistence.*;

@Entity
public class Feedback {
	private @Id @GeneratedValue Long id;
	private String review;
	private int rating;
	private Timestamp datetime;

	protected Feedback() {}

	public Feedback(String review, int rating, Timestamp datetime) {
		this.setReview(review);
		this.setRating(rating);
		this.setDatetime(datetime);
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}
}
