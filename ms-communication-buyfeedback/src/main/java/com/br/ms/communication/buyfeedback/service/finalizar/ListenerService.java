package com.br.ms.communication.buyfeedback.service.finalizar;

import java.io.IOException;

import com.br.ms.communication.buyfeedback.domain.CompraRedis;
import com.br.ms.communication.buyfeedback.gateway.json.CompraFinalizadaJson;
import com.br.ms.communication.buyfeedback.gateway.repository.CompraRedisRepository;
import com.google.gson.Gson;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class ListenerService {

	@Autowired
	private CompraRedisRepository compraRedisRepository;
	
	@Autowired
	private Gson gson;
	
	@RabbitListener(queues="${fila.finalizado}")
    public void onMessage(Message message) throws IOException  {
		
		String json = new String(message.getBody(), "UTF-8");
		
		System.out.println("Mensagem recebida:"+json);
		
		CompraFinalizadaJson compraChaveJson = gson.fromJson(json, CompraFinalizadaJson.class);


		CompraRedis credis = new CompraRedis();
		credis.setId(compraChaveJson.getCompraChaveJson().getChave());
		credis.setMensagem(compraChaveJson.getMensagem());
		credis.setNroCartao(compraChaveJson.getCompraChaveJson().getCompraJson().getNroCartao());
		credis.setValorPassagem(compraChaveJson.getCompraChaveJson().getCompraJson().getValorPassagem());
		credis.setCodigoPassagem(compraChaveJson.getCompraChaveJson().getCompraJson().getCodigoPassagem());
		credis.setPagamentoOK(compraChaveJson.isPagamentoOK());
		System.out.println(compraChaveJson.isPagamentoOK());
		
		System.out.println("Gravando no redis....");
		compraRedisRepository.save(credis);
    }

}