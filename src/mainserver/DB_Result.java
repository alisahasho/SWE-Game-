package mainserver;

import java.sql.Timestamp;

public class DB_Result {
	private String player_0;
	private String player_1;
	private Integer score_0;
	private Integer score_1;
	private Timestamp timestamp;
	public DB_Result(String player_0, String player_1, Integer score_0, Integer score_1, Timestamp timestamp) {
		this.player_0 = player_0;
		this.player_1 = player_1;
		this.score_0 = score_0;
		this.score_1 = score_1;
		this.timestamp = timestamp;
	}
	
	public String get_player_0(){
		return this.player_0;
	}
	
	public String get_player_1(){
		return this.player_1;
	}
	
	public Integer get_score_0(){
		return this.score_0;
	}
	
	public Integer get_score_1(){
		return this.score_1;
	}
	
	public Timestamp get_timestamp(){
		return this.timestamp;
	}
}