package org.crystalpvp.anticheat.database.mongo;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.config.ConfigCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.Collections;

@Getter
public class MongoDB {

	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> logs;

	public MongoDB() {
		ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getMainFileConfig(), "mongo");

		if (!cursor.exists("host")
		    || !cursor.exists("port")
		    || !cursor.exists("database")
		    || !cursor.exists("authentication.enabled")
		    || !cursor.exists("authentication.username")
		    || !cursor.exists("authentication.password")
		    || !cursor.exists("authentication.database")) {
			throw new RuntimeException("Missing configuration option");
		}

		if (cursor.getBoolean("authentication.enabled")) {
			final MongoCredential credential = MongoCredential.createCredential(
					cursor.getString("authentication.username"),
					cursor.getString("authentication.database"),
					cursor.getString("authentication.password").toCharArray()
			);
			this.client = new MongoClient(
					new ServerAddress(cursor.getString("host"), cursor.getInt("port")),
					Collections.singletonList(credential)
			);
		} else {
			this.client = new MongoClient(new ServerAddress(cursor.getString("host"), cursor.getInt("port")));
		}

		this.database = this.client.getDatabase("b198vwp8n92nxup");
		this.logs = this.database.getCollection("alertlogs");
	}

	public static MongoDB getInstance() {
		return CrystalAC.getInstance().getMongo();
	}

}
