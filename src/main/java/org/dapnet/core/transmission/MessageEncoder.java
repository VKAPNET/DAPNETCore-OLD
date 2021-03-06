package org.dapnet.core.transmission;

import java.util.List;

import org.dapnet.core.Settings;
import org.dapnet.core.transmission.TransmissionSettings.PagingProtocolSettings;
import org.dapnet.core.transmission.TransmitterClient.Message;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/**
 * Encodes a {@link PagerMessage} into string.
 * 
 * @author Philipp Thiel
 */
@Sharable
class MessageEncoder extends MessageToMessageEncoder<Message> {

	public static final int MT_SYNCREQUEST = 2;
	public static final int MT_SYNCORDER = 3;
	public static final int MT_SLOTS = 4;
	public static final int MT_NUMERIC = 5;
	public static final int MT_ALPHANUM = 6;

	private static final PagingProtocolSettings settings = Settings.getTransmissionSettings()
			.getPagingProtocolSettings();

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
		PagerMessage pm = msg.getMessage();

		// Mostly adapted from Sven Jung
		// See Diplomarbeit Jansen Page 30
		int type = 0;
		switch (pm.getFunctionalBits()) {
		case ACTIVATION:
		case ALPHANUM:
		case TONE:
			type = MT_ALPHANUM;
			break;
		case NUMERIC:
			type = MT_NUMERIC;
			break;
		}

		String encoded = String.format("#%02X %s:%X:%X:%s:%s\n", msg.getSequenceNumber(), type, settings.getSendSpeed(),
				pm.getAddress(), pm.getFunctionalBits().getValue(), pm.getText());

		out.add(encoded);
	}

}
