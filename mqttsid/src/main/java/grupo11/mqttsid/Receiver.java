package grupo11.mqttsid;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SerializationUtils;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.IMqttClient;

public class Receiver {

	private IMqttClient subscriber;
	private SQLHandler sqlmanager;
	private Map<String,Range> sensorsRange;
	
	public Receiver(IMqttClient subscriber) {
		this.subscriber = subscriber;
		this.sqlmanager = new SQLHandler("jdbc:mysql://localhost:3306/projetosid", "root", "");
		this.sensorsRange = FilterUtils.getRangeFilter();
		serve();
	}
	
	public void serve() {
//		CountDownLatch receivedSignal = new CountDownLatch(10);
		try {
			subscriber.subscribe(Sender.TOPIC, 2, (topic, msg) -> {
			    byte[] payload = msg.getPayload();
			    Document aux = SerializationUtils.deserialize(payload);
			    if(sensorsRange.get(aux.getString("sensor")).isOutOfBounds(aux.getDouble("leitura"))) {
//			    	inserir alerta respetivo
			    	sqlmanager.updateDB("");
			    }
			    System.out.println("receiver:" + aux);
			    sqlmanager.updateDB("insert into medicao(zona, sensor, hora, leitura) values ('" + aux.get("Zona") + "','" + aux.get("Sensor") + "','" + aux.get("Data") + "','" + aux.get("Medicao") + "')");
//			    receivedSignal.countDown();
			});
//			receivedSignal.await(1, TimeUnit.MINUTES);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
