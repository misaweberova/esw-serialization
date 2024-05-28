package cz.esw.serialization;

import cz.esw.serialization.handler.*;

import java.io.*;
import java.net.Socket;
import java.util.Random;

/**
 * @author Marek Cuchý (CVUT)
 */
public class App {

	private static final int DEFAULT_NUMBER_OF_TRANSMISSIONS = 10;

	private static final int SEED = 0;
	private static final int NUMBER_OF_DATASETS = 100;
	private static final int DATASET_SIZE = 10000;

	private final DataProducer producer;

	public App(int seed, int numberOfDatasets, int datasetSize) {
		producer = new DataProducer(new Random(seed), numberOfDatasets, datasetSize);
	}

	public void run(String host, int port, ProtocolType protocol, int numberOfTransmissions) throws IOException {
		for (int i = 0; i < numberOfTransmissions; i++) {
			try (Socket socket = new Socket(host, port)) {
				DataHandler dataHandler = getDataHandler(socket, protocol);
				producer.generateDataAndCheckResults(dataHandler);
			}
		}
	}

	private DataHandler getDataHandler(Socket socket, ProtocolType protocol) throws IOException {
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		return switch (protocol) {
			case JSON -> new JsonDataHandler(is, os);
			case AVRO -> new AvroDataHandler(is, os);
			case PROTO -> new ProtoDataHandler(is, os);
		};
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			System.out.println(
					"Three parameters are required: host, port and protocol used for the transmission. One is optional: number of transmissions");
			return;
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		ProtocolType protocol = ProtocolType.parseType(args[2]);

		int numberOfTransmissions;
		if (args.length > 3) {
			numberOfTransmissions = Integer.parseInt(args[3]);
		} else {
			numberOfTransmissions = DEFAULT_NUMBER_OF_TRANSMISSIONS;
		}
		new App(SEED, NUMBER_OF_DATASETS, DATASET_SIZE).run(host, port, protocol, numberOfTransmissions);
	}
}
