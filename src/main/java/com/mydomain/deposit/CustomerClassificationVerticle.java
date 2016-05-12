package com.mydomain.deposit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

public class CustomerClassificationVerticle extends AbstractVerticle{
	private static MongoClient mongoClient;
	
	@Override
	public void start() throws Exception {
		JsonObject config = new JsonObject().put("connection_string", "mongodb://localhost:27017").put("db_name",
				"custclassification");

		//mongoClient = MongoClient.createShared(vertx, config);
		vertx.eventBus().consumer("depositInfo", depInfo -> {
			System.out.println("Received deposit info: "+depInfo.body());
		});
		System.out.println("Classification service running");
	}

	public static void main(String[] args) {
		ClusterManager mgr = new HazelcastClusterManager();
		VertxOptions options = new VertxOptions().setWorkerPoolSize(10).setClusterManager(mgr);;
		//Vertx vertx = Vertx.factory.vertx(options);
		
		Vertx.clusteredVertx(options, res -> {
		  if (res.succeeded()) {
		    Vertx vertx = res.result();
		    vertx.deployVerticle("com.mydomain.deposit.CustomerClassificationVerticle");
		  } else {
		    // failed!
		  }
		});
	}
}
