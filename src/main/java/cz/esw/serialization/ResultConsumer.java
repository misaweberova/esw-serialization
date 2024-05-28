package cz.esw.serialization;

import cz.esw.serialization.json.DataType;

/**
 * Consumer of results calculated/obtained by a {@link cz.esw.serialization.handler.DataHandler}
 *
 * @author Marek Cuchý (CVUT)
 */
public interface ResultConsumer {

	void acceptMeasurementInfo(int resultId, long timestamp, String measurerName);

	void acceptResult(DataType type, double result);
}
