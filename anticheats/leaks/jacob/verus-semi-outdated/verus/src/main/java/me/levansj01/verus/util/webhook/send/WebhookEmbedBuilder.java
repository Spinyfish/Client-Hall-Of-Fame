package me.levansj01.verus.util.webhook.send;

import java.time.temporal.*;
import discord4j.discordjson.possible.*;
import discord4j.core.spec.*;
import org.jetbrains.annotations.*;
import java.util.function.*;
import java.net.*;
import discord4j.discordjson.json.*;
import java.time.*;
import java.util.*;
import net.dv8tion.jda.api.entities.*;
import java.awt.*;
import org.javacord.api.entity.message.embed.*;

public class WebhookEmbedBuilder
{
    private WebhookEmbed.EmbedTitle title;
    private OffsetDateTime timestamp;
    private final List fields;
    private WebhookEmbed.EmbedFooter footer;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer color;
    private String description;
    private WebhookEmbed.EmbedAuthor author;
    
    public void reset() {
        this.fields.clear();
        this.timestamp = null;
        this.color = null;
        this.description = null;
        this.thumbnailUrl = null;
        this.imageUrl = null;
        this.footer = null;
        this.title = null;
        this.author = null;
    }
    
    private static WebhookEmbed.EmbedField lambda$fromJavacord$4(final EmbedField embedField) {
        return new WebhookEmbed.EmbedField(embedField.isInline(), embedField.getName(), embedField.getValue());
    }
    
    @NotNull
    public static WebhookEmbedBuilder fromD4J(@NotNull final EmbedData embedData) {
        final WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
        final Possible title = embedData.title();
        final Possible description = embedData.description();
        final Possible url = embedData.url();
        final Possible timestamp = embedData.timestamp();
        final Possible color = embedData.color();
        final Possible footer = embedData.footer();
        final Possible image = embedData.image();
        final Possible thumbnail = embedData.thumbnail();
        final Possible author = embedData.author();
        final Possible fields = embedData.fields();
        if (!title.isAbsent()) {
            webhookEmbedBuilder.setTitle(new WebhookEmbed.EmbedTitle((String)title.get(), (String)url.toOptional().orElse(null)));
        }
        if (!description.isAbsent()) {
            webhookEmbedBuilder.setDescription((String)description.get());
        }
        if (!timestamp.isAbsent()) {
            webhookEmbedBuilder.setTimestamp(OffsetDateTime.parse((CharSequence)timestamp.get()));
        }
        if (!color.isAbsent()) {
            webhookEmbedBuilder.setColor((Integer)color.get());
        }
        if (!footer.isAbsent()) {
            webhookEmbedBuilder.setFooter(new WebhookEmbed.EmbedFooter(((EmbedFooterData)footer.get()).text(), (String)((EmbedFooterData)footer.get()).iconUrl().toOptional().orElse(null)));
        }
        if (!image.isAbsent()) {
            webhookEmbedBuilder.setImageUrl((String)((EmbedImageData)image.get()).url().get());
        }
        if (!thumbnail.isAbsent()) {
            webhookEmbedBuilder.setThumbnailUrl((String)((EmbedThumbnailData)thumbnail.get()).url().get());
        }
        if (!author.isAbsent()) {
            final EmbedAuthorData embedAuthorData = (EmbedAuthorData)author.get();
            webhookEmbedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor((String)embedAuthorData.name().get(), (String)embedAuthorData.iconUrl().toOptional().orElse(null), (String)embedAuthorData.url().toOptional().orElse(null)));
        }
        if (!fields.isAbsent()) {
            ((List)fields.get()).stream().map(WebhookEmbedBuilder::lambda$fromD4J$5).forEach(webhookEmbedBuilder::addField);
        }
        return webhookEmbedBuilder;
    }
    
    @NotNull
    public WebhookEmbedBuilder addField(@NotNull final WebhookEmbed.EmbedField embedField) {
        if (this.fields.size() == 25) {
            throw new IllegalStateException("Cannot add more than 25 fields");
        }
        this.fields.add(Objects.requireNonNull(embedField));
        return this;
    }
    
    @NotNull
    public static WebhookEmbedBuilder fromD4J(@NotNull final Consumer consumer) {
        final EmbedCreateSpec embedCreateSpec = new EmbedCreateSpec();
        consumer.accept(embedCreateSpec);
        return fromD4J(embedCreateSpec.asRequest());
    }
    
    @NotNull
    public WebhookEmbedBuilder setColor(@Nullable final Integer color) {
        this.color = color;
        return this;
    }
    
    @NotNull
    public WebhookEmbedBuilder setImageUrl(@Nullable final String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }
    
       private boolean lambda$isFieldsEmpty$0(EmbedField var1) {
      WebhookEmbedBuilder var10001;
      if (var1.getName() != null) {
         var10001 = this;
         if (var1.getValue() != null) {
            boolean var10002 = true;
            return (boolean)var10001;
         }
      }

      var10001 = false;
      return (boolean)var10001;
   }


    
    private static WebhookEmbed.EmbedFooter lambda$fromJavacord$3(final EmbedFooter embedFooter) {
        return new WebhookEmbed.EmbedFooter((String)embedFooter.getText().orElseThrow(NullPointerException::new), (String)embedFooter.getIconUrl().map(URL::toString).orElse(null));
    }
    
    public WebhookEmbedBuilder() {
        this.fields = new ArrayList(10);
    }
    
    private static WebhookEmbed.EmbedField lambda$fromD4J$5(final EmbedFieldData embedFieldData) {
        return new WebhookEmbed.EmbedField((boolean)embedFieldData.inline().toOptional().orElse(false), embedFieldData.name(), embedFieldData.value());
    }
    
    @NotNull
    public WebhookEmbedBuilder setAuthor(@Nullable final WebhookEmbed.EmbedAuthor author) {
        this.author = author;
        return this;
    }
    
    @NotNull
    public WebhookEmbedBuilder setTimestamp(@Nullable final TemporalAccessor temporalAccessor) {
        if (temporalAccessor instanceof Instant) {
            this.timestamp = OffsetDateTime.ofInstant((Instant)temporalAccessor, ZoneId.of("UTC"));
        }
        else {
            this.timestamp = ((temporalAccessor == null) ? null : OffsetDateTime.from(temporalAccessor));
        }
        return this;
    }
    
    @NotNull
    public WebhookEmbed build() {
        if (this != null) {
            throw new IllegalStateException("Cannot build an empty embed");
        }
        return new WebhookEmbed(this.timestamp, this.color, this.description, this.thumbnailUrl, this.imageUrl, this.footer, this.title, this.author, (List)new ArrayList(this.fields));
    }
    
    @NotNull
    public static WebhookEmbedBuilder fromJDA(@NotNull final MessageEmbed messageEmbed) {
        final WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
        final String url = messageEmbed.getUrl();
        final String title = messageEmbed.getTitle();
        final String description = messageEmbed.getDescription();
        final MessageEmbed.Thumbnail thumbnail = messageEmbed.getThumbnail();
        final MessageEmbed.AuthorInfo author = messageEmbed.getAuthor();
        final MessageEmbed.Footer footer = messageEmbed.getFooter();
        final MessageEmbed.ImageInfo image = messageEmbed.getImage();
        final List fields = messageEmbed.getFields();
        final int colorRaw = messageEmbed.getColorRaw();
        final OffsetDateTime timestamp = messageEmbed.getTimestamp();
        if (title != null) {
            webhookEmbedBuilder.setTitle(new WebhookEmbed.EmbedTitle(title, url));
        }
        if (description != null) {
            webhookEmbedBuilder.setDescription(description);
        }
        if (thumbnail != null) {
            webhookEmbedBuilder.setThumbnailUrl(thumbnail.getUrl());
        }
        if (author != null) {
            webhookEmbedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor(author.getName(), author.getIconUrl(), author.getUrl()));
        }
        if (footer != null) {
            webhookEmbedBuilder.setFooter(new WebhookEmbed.EmbedFooter(footer.getText(), footer.getIconUrl()));
        }
        if (image != null) {
            webhookEmbedBuilder.setImageUrl(image.getUrl());
        }
        if (!fields.isEmpty()) {
            fields.forEach(WebhookEmbedBuilder::lambda$fromJDA$1);
        }
        if (colorRaw != 536870911) {
            webhookEmbedBuilder.setColor(colorRaw);
        }
        if (timestamp != null) {
            webhookEmbedBuilder.setTimestamp(timestamp);
        }
        return webhookEmbedBuilder;
    }
    
    @NotNull
    public WebhookEmbedBuilder setThumbnailUrl(@Nullable final String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }
    
    @NotNull
    public WebhookEmbedBuilder setFooter(@Nullable final WebhookEmbed.EmbedFooter footer) {
        this.footer = footer;
        return this;
    }
    
    private static void lambda$fromJDA$1(final WebhookEmbedBuilder webhookEmbedBuilder, final MessageEmbed.Field field) {
        webhookEmbedBuilder.addField(new WebhookEmbed.EmbedField(field.isInline(), field.getName(), field.getValue()));
    }
    
    @NotNull
    public WebhookEmbedBuilder setDescription(@Nullable final String description) {
        this.description = description;
        return this;
    }
    
    public WebhookEmbedBuilder(@Nullable final WebhookEmbed webhookEmbed) {
        this();
        if (webhookEmbed != null) {
            this.timestamp = webhookEmbed.getTimestamp();
            this.color = webhookEmbed.getColor();
            this.description = webhookEmbed.getDescription();
            this.thumbnailUrl = webhookEmbed.getThumbnailUrl();
            this.imageUrl = webhookEmbed.getImageUrl();
            this.footer = webhookEmbed.getFooter();
            this.title = webhookEmbed.getTitle();
            this.author = webhookEmbed.getAuthor();
            this.fields.addAll(webhookEmbed.getFields());
        }
    }
    
    private static void lambda$fromJavacord$2(final WebhookEmbedBuilder webhookEmbedBuilder, final Embed embed, final String s) {
        webhookEmbedBuilder.setTitle(new WebhookEmbed.EmbedTitle(s, (String)embed.getUrl().map(URL::toString).orElse(null)));
    }
    
    @NotNull
    public static WebhookEmbedBuilder fromJavacord(@NotNull final Embed embed) {
        final WebhookEmbedBuilder webhookEmbedBuilder = new WebhookEmbedBuilder();
        embed.getTitle().ifPresent(WebhookEmbedBuilder::lambda$fromJavacord$2);
        embed.getDescription().ifPresent(webhookEmbedBuilder::setDescription);
        embed.getTimestamp().ifPresent(webhookEmbedBuilder::setTimestamp);
        embed.getColor().map(Color::getRGB).ifPresent(webhookEmbedBuilder::setColor);
        embed.getFooter().map(WebhookEmbedBuilder::lambda$fromJavacord$3).ifPresent(webhookEmbedBuilder::setFooter);
        embed.getImage().map(EmbedImage::getUrl).map(URL::toString).ifPresent(webhookEmbedBuilder::setImageUrl);
        embed.getThumbnail().map(EmbedThumbnail::getUrl).map(URL::toString).ifPresent(webhookEmbedBuilder::setThumbnailUrl);
        embed.getFields().stream().map(WebhookEmbedBuilder::lambda$fromJavacord$4).forEach(webhookEmbedBuilder::addField);
        return webhookEmbedBuilder;
    }
    
    @NotNull
    public WebhookEmbedBuilder setTitle(@Nullable final WebhookEmbed.EmbedTitle title) {
        this.title = title;
        return this;
    }
}
