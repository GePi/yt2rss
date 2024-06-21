package farm.giggle.yt2rss.exportfeeds;

import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.services.ChannelService;
import farm.giggle.yt2rss.services.UserService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.StringWriter;

public abstract class FeedProducerAbstract implements FeedProducer {
    protected final ChannelService channelService;
    protected final UserService userService;

    public FeedProducerAbstract(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    protected <T> String marshal(T feed) throws JaxbMarshallerException {
        String result;
        try {
            JAXBContext context = JAXBContext.newInstance(feed.getClass());
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            mar.marshal(feed, sw);
            result = sw.toString();
        } catch (JAXBException e) {
            throw new JaxbMarshallerException(e);
        }
        return result;
    }
}
