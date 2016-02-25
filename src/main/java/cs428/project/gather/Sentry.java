package cs428.project.gather;

import java.lang.*;
import java.util.*;
import net.kencochrane.raven.*;
import net.kencochrane.raven.event.*;

// Documentation at http://grepcode.com/file/repo1.maven.org/maven2/net.kencochrane.raven/raven/4.0/net/kencochrane/raven
// http://stackoverflow.com/questions/10568275/noclassdeffounderror-on-maven-dependency

class Sentry {
    private static Raven raven;

    public static void setDsn(String rawDsn) {
        raven = RavenFactory.ravenInstance(new Dsn(rawDsn));
    }

    private static void setDsnIfNull(String rawDsn) {
        if (raven == null) setDsn(rawDsn);
    }

    public static <T> void sendError(Class<T> klass, Throwable throwable) {
        send(klass, Event.Level.ERROR, throwable, null, null, null);
    }

    public static <T> void sendError(Class<T> klass, Throwable throwable, String customMessage) {
        send(klass, Event.Level.ERROR, throwable, customMessage, null, null);
    }

    public static <T> void sendError(Class<T> klass, Throwable throwable, String customMessage, Map<String, String> tags) {
        send(klass, Event.Level.ERROR, throwable, customMessage, tags, null);
    }

    public static <T> void sendError(Class<T> klass, Throwable throwable, String customMessage, Map<String, String> tags, Map<String, Object> extras) {
        send(klass, Event.Level.ERROR, throwable, customMessage, tags, extras);
    }

    public static <T> void sendWarning(Class<T> klass, Throwable throwable) {
        send(klass, Event.Level.WARNING, throwable, null, null, null);
    }

    public static <T> void sendWarning(Class<T> klass, Throwable throwable, String customMessage) {
        send(klass, Event.Level.WARNING, throwable, customMessage, null, null);
    }

    public static <T> void sendWarning(Class<T> klass, Throwable throwable, String customMessage, Map<String, String> tags) {
        send(klass, Event.Level.WARNING, throwable, customMessage, tags, null);
    }

    public static <T> void sendWarning(Class<T> klass, Throwable throwable, String customMessage, Map<String, String> tags, Map<String, Object> extras) {
        send(klass, Event.Level.WARNING, throwable, customMessage, tags, extras);
    }

    public static <T> void send(Class<T> klass, Event.Level level, Throwable throwable, String customMessage, Map<String, String> tags, Map<String, Object> extras) {
        setDsnIfNull("https://3a36312fea814df3b3baf7199c43b0c8:2250fd649bfa473f91f09d397c62f19b@sentry-gatherdemo.rhcloud.com/2");
        System.out.println( "Sending exception to Sentry with " + raven);

        GatherEventBuilder builder = (GatherEventBuilder)(new GatherEventBuilder(throwable, customMessage)
            .addTags(tags)
            .addExtras(extras)
            .setLevel(level)
            .setLogger(klass.getName()));

        raven.runBuilderHelpers(builder); // Optional
        raven.sendEvent(builder.build());

        System.out.println( "Finished sending " + level + " to Sentry" );
    }
}

class GatherEventBuilder extends EventBuilder {
    GatherEventBuilder(Throwable throwable, String customMessage) {
        super();
        setMessage(customMessage + "\n" + throwable.getMessage())
                .setCulprit(throwable.getStackTrace()[0]);
    }

    GatherEventBuilder addExtras(Map<String, Object> extras) {
        if (extras == null) return this;
        for (Map.Entry<String, Object> entry : extras.entrySet()) {
            addExtra(entry.getKey(), entry.getValue());
        } return this;
    }

    GatherEventBuilder addTags(Map<String, String> tags) {
        if (tags == null) return this;
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            addTag(entry.getKey(), entry.getValue());
        } return this;
    }
}
