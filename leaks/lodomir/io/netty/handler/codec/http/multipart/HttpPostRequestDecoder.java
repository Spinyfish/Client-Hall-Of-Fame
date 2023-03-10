/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.CaseIgnoringComparator;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostRequestDecoder {
    private static final int DEFAULT_DISCARD_THRESHOLD = 0xA00000;
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private final Charset charset;
    private boolean bodyToDecode;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<InterfaceHttpData>();
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<CharSequence, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
    private ByteBuf undecodedChunk;
    private boolean isMultipart;
    private int bodyListHttpDataRank;
    private String multipartDataBoundary;
    private String multipartMixedBoundary;
    private MultiPartStatus currentStatus = MultiPartStatus.NOTSTARTED;
    private Map<String, Attribute> currentFieldAttributes;
    private FileUpload currentFileUpload;
    private Attribute currentAttribute;
    private boolean destroyed;
    private int discardThreshold = 0xA00000;

    public HttpPostRequestDecoder(HttpRequest request) throws ErrorDataDecoderException, IncompatibleDataDecoderException {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request) throws ErrorDataDecoderException, IncompatibleDataDecoderException {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) throws ErrorDataDecoderException, IncompatibleDataDecoderException {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        if (request == null) {
            throw new NullPointerException("request");
        }
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.request = request;
        HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT) || method.equals(HttpMethod.PATCH)) {
            this.bodyToDecode = true;
        }
        this.charset = charset;
        this.factory = factory;
        String contentType = this.request.headers().get("Content-Type");
        if (contentType != null) {
            this.checkMultipart(contentType);
        } else {
            this.isMultipart = false;
        }
        if (!this.bodyToDecode) {
            throw new IncompatibleDataDecoderException("No Body to decode");
        }
        if (request instanceof HttpContent) {
            this.offer((HttpContent)((Object)request));
        } else {
            this.undecodedChunk = Unpooled.buffer();
            this.parseBody();
        }
    }

    private void checkMultipart(String contentType) throws ErrorDataDecoderException {
        String[] headerContentType = HttpPostRequestDecoder.splitHeaderContentType(contentType);
        if (headerContentType[0].toLowerCase().startsWith("multipart/form-data") && headerContentType[1].toLowerCase().startsWith("boundary")) {
            int index;
            String bound;
            String[] boundary = StringUtil.split(headerContentType[1], '=');
            if (boundary.length != 2) {
                throw new ErrorDataDecoderException("Needs a boundary value");
            }
            if (boundary[1].charAt(0) == '\"' && (bound = boundary[1].trim()).charAt(index = bound.length() - 1) == '\"') {
                boundary[1] = bound.substring(1, index);
            }
            this.multipartDataBoundary = "--" + boundary[1];
            this.isMultipart = true;
            this.currentStatus = MultiPartStatus.HEADERDELIMITER;
        } else {
            this.isMultipart = false;
        }
    }

    private void checkDestroyed() {
        if (this.destroyed) {
            throw new IllegalStateException(HttpPostRequestDecoder.class.getSimpleName() + " was destroyed already");
        }
    }

    public boolean isMultipart() {
        this.checkDestroyed();
        return this.isMultipart;
    }

    public void setDiscardThreshold(int discardThreshold) {
        if (discardThreshold < 0) {
            throw new IllegalArgumentException("discardThreshold must be >= 0");
        }
        this.discardThreshold = discardThreshold;
    }

    public int getDiscardThreshold() {
        return this.discardThreshold;
    }

    public List<InterfaceHttpData> getBodyHttpDatas() throws NotEnoughDataDecoderException {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new NotEnoughDataDecoderException();
        }
        return this.bodyListHttpData;
    }

    public List<InterfaceHttpData> getBodyHttpDatas(String name) throws NotEnoughDataDecoderException {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new NotEnoughDataDecoderException();
        }
        return this.bodyMapHttpData.get(name);
    }

    public InterfaceHttpData getBodyHttpData(String name) throws NotEnoughDataDecoderException {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new NotEnoughDataDecoderException();
        }
        List<InterfaceHttpData> list = this.bodyMapHttpData.get(name);
        if (list != null) {
            return list.get(0);
        }
        return null;
    }

    public HttpPostRequestDecoder offer(HttpContent content) throws ErrorDataDecoderException {
        this.checkDestroyed();
        ByteBuf buf = content.content();
        if (this.undecodedChunk == null) {
            this.undecodedChunk = buf.copy();
        } else {
            this.undecodedChunk.writeBytes(buf);
        }
        if (content instanceof LastHttpContent) {
            this.isLastChunk = true;
        }
        this.parseBody();
        if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
            this.undecodedChunk.discardReadBytes();
        }
        return this;
    }

    public boolean hasNext() throws EndOfDataDecoderException {
        this.checkDestroyed();
        if (this.currentStatus == MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
            throw new EndOfDataDecoderException();
        }
        return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
    }

    public InterfaceHttpData next() throws EndOfDataDecoderException {
        this.checkDestroyed();
        if (this.hasNext()) {
            return this.bodyListHttpData.get(this.bodyListHttpDataRank++);
        }
        return null;
    }

    private void parseBody() throws ErrorDataDecoderException {
        if (this.currentStatus == MultiPartStatus.PREEPILOGUE || this.currentStatus == MultiPartStatus.EPILOGUE) {
            if (this.isLastChunk) {
                this.currentStatus = MultiPartStatus.EPILOGUE;
            }
            return;
        }
        if (this.isMultipart) {
            this.parseBodyMultipart();
        } else {
            this.parseBodyAttributes();
        }
    }

    protected void addHttpData(InterfaceHttpData data) {
        if (data == null) {
            return;
        }
        List<InterfaceHttpData> datas = this.bodyMapHttpData.get(data.getName());
        if (datas == null) {
            datas = new ArrayList<InterfaceHttpData>(1);
            this.bodyMapHttpData.put(data.getName(), datas);
        }
        datas.add(data);
        this.bodyListHttpData.add(data);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void parseBodyAttributesStandard() throws ErrorDataDecoderException {
        int firstpos;
        int currentpos = firstpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == MultiPartStatus.NOTSTARTED) {
            this.currentStatus = MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
            int ampersandpos;
            block7: while (this.undecodedChunk.isReadable() && contRead) {
                char read = (char)this.undecodedChunk.readUnsignedByte();
                ++currentpos;
                switch (this.currentStatus) {
                    case DISPOSITION: {
                        String key;
                        if (read == '=') {
                            this.currentStatus = MultiPartStatus.FIELD;
                            int equalpos = currentpos - 1;
                            key = HttpPostRequestDecoder.decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, key);
                            firstpos = currentpos;
                            continue block7;
                        }
                        if (read != '&') continue block7;
                        this.currentStatus = MultiPartStatus.DISPOSITION;
                        ampersandpos = currentpos - 1;
                        key = HttpPostRequestDecoder.decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                        this.currentAttribute = this.factory.createAttribute(this.request, key);
                        this.currentAttribute.setValue("");
                        this.addHttpData(this.currentAttribute);
                        this.currentAttribute = null;
                        firstpos = currentpos;
                        contRead = true;
                        continue block7;
                    }
                    case FIELD: {
                        if (read == '&') {
                            this.currentStatus = MultiPartStatus.DISPOSITION;
                            ampersandpos = currentpos - 1;
                            this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue block7;
                        }
                        if (read == '\r') {
                            if (this.undecodedChunk.isReadable()) {
                                read = (char)this.undecodedChunk.readUnsignedByte();
                                if (read != '\n') throw new ErrorDataDecoderException("Bad end of line");
                                this.currentStatus = MultiPartStatus.PREEPILOGUE;
                                ampersandpos = ++currentpos - 2;
                                this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                continue block7;
                            }
                            --currentpos;
                            continue block7;
                        }
                        if (read != '\n') continue block7;
                        this.currentStatus = MultiPartStatus.PREEPILOGUE;
                        ampersandpos = currentpos - 1;
                        this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                        firstpos = currentpos;
                        contRead = false;
                        continue block7;
                    }
                }
                contRead = false;
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                } else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = MultiPartStatus.EPILOGUE;
                this.undecodedChunk.readerIndex(firstpos);
                return;
            }
            if (contRead && this.currentAttribute != null) {
                if (this.currentStatus == MultiPartStatus.FIELD) {
                    this.currentAttribute.addContent(this.undecodedChunk.copy(firstpos, currentpos - firstpos), false);
                    firstpos = currentpos;
                }
                this.undecodedChunk.readerIndex(firstpos);
                return;
            }
            this.undecodedChunk.readerIndex(firstpos);
            return;
        }
        catch (ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw e;
        }
        catch (IOException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new ErrorDataDecoderException(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void parseBodyAttributes() throws ErrorDataDecoderException {
        int firstpos;
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
            this.parseBodyAttributesStandard();
            return;
        }
        int currentpos = firstpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == MultiPartStatus.NOTSTARTED) {
            this.currentStatus = MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
            int ampersandpos;
            block9: while (sao.pos < sao.limit) {
                char read = (char)(sao.bytes[sao.pos++] & 0xFF);
                ++currentpos;
                switch (this.currentStatus) {
                    case DISPOSITION: {
                        String key;
                        if (read == '=') {
                            this.currentStatus = MultiPartStatus.FIELD;
                            int equalpos = currentpos - 1;
                            key = HttpPostRequestDecoder.decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, key);
                            firstpos = currentpos;
                            continue block9;
                        }
                        if (read != '&') continue block9;
                        this.currentStatus = MultiPartStatus.DISPOSITION;
                        ampersandpos = currentpos - 1;
                        key = HttpPostRequestDecoder.decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                        this.currentAttribute = this.factory.createAttribute(this.request, key);
                        this.currentAttribute.setValue("");
                        this.addHttpData(this.currentAttribute);
                        this.currentAttribute = null;
                        firstpos = currentpos;
                        contRead = true;
                        continue block9;
                    }
                    case FIELD: {
                        if (read == '&') {
                            this.currentStatus = MultiPartStatus.DISPOSITION;
                            ampersandpos = currentpos - 1;
                            this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue block9;
                        }
                        if (read == '\r') {
                            if (sao.pos < sao.limit) {
                                read = (char)(sao.bytes[sao.pos++] & 0xFF);
                                ++currentpos;
                                if (read != '\n') {
                                    sao.setReadPosition(0);
                                    throw new ErrorDataDecoderException("Bad end of line");
                                }
                                this.currentStatus = MultiPartStatus.PREEPILOGUE;
                                ampersandpos = currentpos - 2;
                                sao.setReadPosition(0);
                                this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                break block9;
                            }
                            if (sao.limit <= 0) continue block9;
                            --currentpos;
                            continue block9;
                        }
                        if (read != '\n') continue block9;
                        this.currentStatus = MultiPartStatus.PREEPILOGUE;
                        ampersandpos = currentpos - 1;
                        sao.setReadPosition(0);
                        this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                        firstpos = currentpos;
                        contRead = false;
                        break block9;
                    }
                }
                sao.setReadPosition(0);
                contRead = false;
                break;
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                } else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = MultiPartStatus.EPILOGUE;
                this.undecodedChunk.readerIndex(firstpos);
                return;
            }
            if (contRead && this.currentAttribute != null) {
                if (this.currentStatus == MultiPartStatus.FIELD) {
                    this.currentAttribute.addContent(this.undecodedChunk.copy(firstpos, currentpos - firstpos), false);
                    firstpos = currentpos;
                }
                this.undecodedChunk.readerIndex(firstpos);
                return;
            }
            this.undecodedChunk.readerIndex(firstpos);
            return;
        }
        catch (ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw e;
        }
        catch (IOException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new ErrorDataDecoderException(e);
        }
    }

    private void setFinalBuffer(ByteBuf buffer) throws ErrorDataDecoderException, IOException {
        this.currentAttribute.addContent(buffer, true);
        String value = HttpPostRequestDecoder.decodeAttribute(this.currentAttribute.getByteBuf().toString(this.charset), this.charset);
        this.currentAttribute.setValue(value);
        this.addHttpData(this.currentAttribute);
        this.currentAttribute = null;
    }

    private static String decodeAttribute(String s, Charset charset) throws ErrorDataDecoderException {
        try {
            return QueryStringDecoder.decodeComponent(s, charset);
        }
        catch (IllegalArgumentException e) {
            throw new ErrorDataDecoderException("Bad string: '" + s + '\'', e);
        }
    }

    private void parseBodyMultipart() throws ErrorDataDecoderException {
        if (this.undecodedChunk == null || this.undecodedChunk.readableBytes() == 0) {
            return;
        }
        InterfaceHttpData data = this.decodeMultipart(this.currentStatus);
        while (data != null) {
            this.addHttpData(data);
            if (this.currentStatus == MultiPartStatus.PREEPILOGUE || this.currentStatus == MultiPartStatus.EPILOGUE) break;
            data = this.decodeMultipart(this.currentStatus);
        }
    }

    private InterfaceHttpData decodeMultipart(MultiPartStatus state) throws ErrorDataDecoderException {
        switch (state) {
            case NOTSTARTED: {
                throw new ErrorDataDecoderException("Should not be called with the current getStatus");
            }
            case PREAMBLE: {
                throw new ErrorDataDecoderException("Should not be called with the current getStatus");
            }
            case HEADERDELIMITER: {
                return this.findMultipartDelimiter(this.multipartDataBoundary, MultiPartStatus.DISPOSITION, MultiPartStatus.PREEPILOGUE);
            }
            case DISPOSITION: {
                return this.findMultipartDisposition();
            }
            case FIELD: {
                Charset localCharset = null;
                Attribute charsetAttribute = this.currentFieldAttributes.get("charset");
                if (charsetAttribute != null) {
                    try {
                        localCharset = Charset.forName(charsetAttribute.getValue());
                    }
                    catch (IOException e) {
                        throw new ErrorDataDecoderException(e);
                    }
                }
                Attribute nameAttribute = this.currentFieldAttributes.get("name");
                if (this.currentAttribute == null) {
                    try {
                        this.currentAttribute = this.factory.createAttribute(this.request, HttpPostRequestDecoder.cleanString(nameAttribute.getValue()));
                    }
                    catch (NullPointerException e) {
                        throw new ErrorDataDecoderException(e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new ErrorDataDecoderException(e);
                    }
                    catch (IOException e) {
                        throw new ErrorDataDecoderException(e);
                    }
                    if (localCharset != null) {
                        this.currentAttribute.setCharset(localCharset);
                    }
                }
                try {
                    this.loadFieldMultipart(this.multipartDataBoundary);
                }
                catch (NotEnoughDataDecoderException e) {
                    return null;
                }
                Attribute finalAttribute = this.currentAttribute;
                this.currentAttribute = null;
                this.currentFieldAttributes = null;
                this.currentStatus = MultiPartStatus.HEADERDELIMITER;
                return finalAttribute;
            }
            case FILEUPLOAD: {
                return this.getFileUpload(this.multipartDataBoundary);
            }
            case MIXEDDELIMITER: {
                return this.findMultipartDelimiter(this.multipartMixedBoundary, MultiPartStatus.MIXEDDISPOSITION, MultiPartStatus.HEADERDELIMITER);
            }
            case MIXEDDISPOSITION: {
                return this.findMultipartDisposition();
            }
            case MIXEDFILEUPLOAD: {
                return this.getFileUpload(this.multipartMixedBoundary);
            }
            case PREEPILOGUE: {
                return null;
            }
            case EPILOGUE: {
                return null;
            }
        }
        throw new ErrorDataDecoderException("Shouldn't reach here.");
    }

    void skipControlCharacters() throws NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e) {
            try {
                this.skipControlCharactersStandard();
            }
            catch (IndexOutOfBoundsException e1) {
                throw new NotEnoughDataDecoderException(e1);
            }
            return;
        }
        while (sao.pos < sao.limit) {
            char c;
            if (Character.isISOControl(c = (char)(sao.bytes[sao.pos++] & 0xFF)) || Character.isWhitespace(c)) continue;
            sao.setReadPosition(1);
            return;
        }
        throw new NotEnoughDataDecoderException("Access out of bounds");
    }

    void skipControlCharactersStandard() {
        char c;
        while (Character.isISOControl(c = (char)this.undecodedChunk.readUnsignedByte()) || Character.isWhitespace(c)) {
        }
        this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
    }

    private InterfaceHttpData findMultipartDelimiter(String delimiter, MultiPartStatus dispositionStatus, MultiPartStatus closeDelimiterStatus) throws ErrorDataDecoderException {
        String newline;
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            this.skipControlCharacters();
        }
        catch (NotEnoughDataDecoderException e1) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
        this.skipOneLine();
        try {
            newline = this.readDelimiter(delimiter);
        }
        catch (NotEnoughDataDecoderException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
        if (newline.equals(delimiter)) {
            this.currentStatus = dispositionStatus;
            return this.decodeMultipart(dispositionStatus);
        }
        if (newline.equals(delimiter + "--")) {
            this.currentStatus = closeDelimiterStatus;
            if (this.currentStatus == MultiPartStatus.HEADERDELIMITER) {
                this.currentFieldAttributes = null;
                return this.decodeMultipart(MultiPartStatus.HEADERDELIMITER);
            }
            return null;
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new ErrorDataDecoderException("No Multipart delimiter found");
    }

    private InterfaceHttpData findMultipartDisposition() throws ErrorDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        if (this.currentStatus == MultiPartStatus.DISPOSITION) {
            this.currentFieldAttributes = new TreeMap<CharSequence, Attribute>(CaseIgnoringComparator.INSTANCE);
        }
        while (!this.skipOneLine()) {
            String newline;
            try {
                this.skipControlCharacters();
                newline = this.readLine();
            }
            catch (NotEnoughDataDecoderException e) {
                this.undecodedChunk.readerIndex(readerIndex);
                return null;
            }
            String[] contents = HttpPostRequestDecoder.splitMultipartHeader(newline);
            if (contents[0].equalsIgnoreCase("Content-Disposition")) {
                boolean checkSecondArg;
                if (this.currentStatus == MultiPartStatus.DISPOSITION) {
                    checkSecondArg = contents[1].equalsIgnoreCase("form-data");
                } else {
                    boolean bl = checkSecondArg = contents[1].equalsIgnoreCase("attachment") || contents[1].equalsIgnoreCase("file");
                }
                if (!checkSecondArg) continue;
                for (int i = 2; i < contents.length; ++i) {
                    Attribute attribute;
                    String[] values = StringUtil.split(contents[i], '=');
                    try {
                        String name = HttpPostRequestDecoder.cleanString(values[0]);
                        String value = values[1];
                        value = "filename".equals(name) ? value.substring(1, value.length() - 1) : HttpPostRequestDecoder.cleanString(value);
                        attribute = this.factory.createAttribute(this.request, name, value);
                    }
                    catch (NullPointerException e) {
                        throw new ErrorDataDecoderException(e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new ErrorDataDecoderException(e);
                    }
                    this.currentFieldAttributes.put(attribute.getName(), attribute);
                }
                continue;
            }
            if (contents[0].equalsIgnoreCase("Content-Transfer-Encoding")) {
                Attribute attribute;
                try {
                    attribute = this.factory.createAttribute(this.request, "Content-Transfer-Encoding", HttpPostRequestDecoder.cleanString(contents[1]));
                }
                catch (NullPointerException e) {
                    throw new ErrorDataDecoderException(e);
                }
                catch (IllegalArgumentException e) {
                    throw new ErrorDataDecoderException(e);
                }
                this.currentFieldAttributes.put("Content-Transfer-Encoding", attribute);
                continue;
            }
            if (contents[0].equalsIgnoreCase("Content-Length")) {
                Attribute attribute;
                try {
                    attribute = this.factory.createAttribute(this.request, "Content-Length", HttpPostRequestDecoder.cleanString(contents[1]));
                }
                catch (NullPointerException e) {
                    throw new ErrorDataDecoderException(e);
                }
                catch (IllegalArgumentException e) {
                    throw new ErrorDataDecoderException(e);
                }
                this.currentFieldAttributes.put("Content-Length", attribute);
                continue;
            }
            if (contents[0].equalsIgnoreCase("Content-Type")) {
                if (contents[1].equalsIgnoreCase("multipart/mixed")) {
                    if (this.currentStatus == MultiPartStatus.DISPOSITION) {
                        String[] values = StringUtil.split(contents[2], '=');
                        this.multipartMixedBoundary = "--" + values[1];
                        this.currentStatus = MultiPartStatus.MIXEDDELIMITER;
                        return this.decodeMultipart(MultiPartStatus.MIXEDDELIMITER);
                    }
                    throw new ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
                }
                for (int i = 1; i < contents.length; ++i) {
                    Attribute attribute;
                    if (contents[i].toLowerCase().startsWith("charset")) {
                        Attribute attribute2;
                        String[] values = StringUtil.split(contents[i], '=');
                        try {
                            attribute2 = this.factory.createAttribute(this.request, "charset", HttpPostRequestDecoder.cleanString(values[1]));
                        }
                        catch (NullPointerException e) {
                            throw new ErrorDataDecoderException(e);
                        }
                        catch (IllegalArgumentException e) {
                            throw new ErrorDataDecoderException(e);
                        }
                        this.currentFieldAttributes.put("charset", attribute2);
                        continue;
                    }
                    try {
                        attribute = this.factory.createAttribute(this.request, HttpPostRequestDecoder.cleanString(contents[0]), contents[i]);
                    }
                    catch (NullPointerException e) {
                        throw new ErrorDataDecoderException(e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new ErrorDataDecoderException(e);
                    }
                    this.currentFieldAttributes.put(attribute.getName(), attribute);
                }
                continue;
            }
            throw new ErrorDataDecoderException("Unknown Params: " + newline);
        }
        Attribute filenameAttribute = this.currentFieldAttributes.get("filename");
        if (this.currentStatus == MultiPartStatus.DISPOSITION) {
            if (filenameAttribute != null) {
                this.currentStatus = MultiPartStatus.FILEUPLOAD;
                return this.decodeMultipart(MultiPartStatus.FILEUPLOAD);
            }
            this.currentStatus = MultiPartStatus.FIELD;
            return this.decodeMultipart(MultiPartStatus.FIELD);
        }
        if (filenameAttribute != null) {
            this.currentStatus = MultiPartStatus.MIXEDFILEUPLOAD;
            return this.decodeMultipart(MultiPartStatus.MIXEDFILEUPLOAD);
        }
        throw new ErrorDataDecoderException("Filename not found");
    }

    protected InterfaceHttpData getFileUpload(String delimiter) throws ErrorDataDecoderException {
        Attribute charsetAttribute;
        Attribute encoding = this.currentFieldAttributes.get("Content-Transfer-Encoding");
        Charset localCharset = this.charset;
        HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
        if (encoding != null) {
            String code;
            try {
                code = encoding.getValue().toLowerCase();
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
            if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
                localCharset = HttpPostBodyUtil.US_ASCII;
            } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
                localCharset = HttpPostBodyUtil.ISO_8859_1;
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
            } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
            } else {
                throw new ErrorDataDecoderException("TransferEncoding Unknown: " + code);
            }
        }
        if ((charsetAttribute = this.currentFieldAttributes.get("charset")) != null) {
            try {
                localCharset = Charset.forName(charsetAttribute.getValue());
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
        }
        if (this.currentFileUpload == null) {
            long size;
            Attribute filenameAttribute = this.currentFieldAttributes.get("filename");
            Attribute nameAttribute = this.currentFieldAttributes.get("name");
            Attribute contentTypeAttribute = this.currentFieldAttributes.get("Content-Type");
            if (contentTypeAttribute == null) {
                throw new ErrorDataDecoderException("Content-Type is absent but required");
            }
            Attribute lengthAttribute = this.currentFieldAttributes.get("Content-Length");
            try {
                size = lengthAttribute != null ? Long.parseLong(lengthAttribute.getValue()) : 0L;
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
            catch (NumberFormatException e) {
                size = 0L;
            }
            try {
                this.currentFileUpload = this.factory.createFileUpload(this.request, HttpPostRequestDecoder.cleanString(nameAttribute.getValue()), HttpPostRequestDecoder.cleanString(filenameAttribute.getValue()), contentTypeAttribute.getValue(), mechanism.value(), localCharset, size);
            }
            catch (NullPointerException e) {
                throw new ErrorDataDecoderException(e);
            }
            catch (IllegalArgumentException e) {
                throw new ErrorDataDecoderException(e);
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
        }
        try {
            this.readFileUploadByteMultipart(delimiter);
        }
        catch (NotEnoughDataDecoderException e) {
            return null;
        }
        if (this.currentFileUpload.isCompleted()) {
            if (this.currentStatus == MultiPartStatus.FILEUPLOAD) {
                this.currentStatus = MultiPartStatus.HEADERDELIMITER;
                this.currentFieldAttributes = null;
            } else {
                this.currentStatus = MultiPartStatus.MIXEDDELIMITER;
                this.cleanMixedAttributes();
            }
            FileUpload fileUpload = this.currentFileUpload;
            this.currentFileUpload = null;
            return fileUpload;
        }
        return null;
    }

    public void destroy() {
        this.checkDestroyed();
        this.cleanFiles();
        this.destroyed = true;
        if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
            this.undecodedChunk.release();
            this.undecodedChunk = null;
        }
        for (int i = this.bodyListHttpDataRank; i < this.bodyListHttpData.size(); ++i) {
            this.bodyListHttpData.get(i).release();
        }
    }

    public void cleanFiles() {
        this.checkDestroyed();
        this.factory.cleanRequestHttpDatas(this.request);
    }

    public void removeHttpDataFromClean(InterfaceHttpData data) {
        this.checkDestroyed();
        this.factory.removeHttpDataFromClean(this.request, data);
    }

    private void cleanMixedAttributes() {
        this.currentFieldAttributes.remove("charset");
        this.currentFieldAttributes.remove("Content-Length");
        this.currentFieldAttributes.remove("Content-Transfer-Encoding");
        this.currentFieldAttributes.remove("Content-Type");
        this.currentFieldAttributes.remove("filename");
    }

    private String readLineStandard() throws NotEnoughDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            ByteBuf line = Unpooled.buffer(64);
            while (this.undecodedChunk.isReadable()) {
                byte nextByte = this.undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = this.undecodedChunk.getByte(this.undecodedChunk.readerIndex());
                    if (nextByte == 10) {
                        this.undecodedChunk.skipBytes(1);
                        return line.toString(this.charset);
                    }
                    line.writeByte(13);
                    continue;
                }
                if (nextByte == 10) {
                    return line.toString(this.charset);
                }
                line.writeByte(nextByte);
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e);
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new NotEnoughDataDecoderException();
    }

    private String readLine() throws NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
            return this.readLineStandard();
        }
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            ByteBuf line = Unpooled.buffer(64);
            while (sao.pos < sao.limit) {
                byte nextByte;
                if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                    if (sao.pos < sao.limit) {
                        if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                            sao.setReadPosition(0);
                            return line.toString(this.charset);
                        }
                        --sao.pos;
                        line.writeByte(13);
                        continue;
                    }
                    line.writeByte(nextByte);
                    continue;
                }
                if (nextByte == 10) {
                    sao.setReadPosition(0);
                    return line.toString(this.charset);
                }
                line.writeByte(nextByte);
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e);
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new NotEnoughDataDecoderException();
    }

    private String readDelimiterStandard(String delimiter) throws NotEnoughDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            byte nextByte;
            StringBuilder sb = new StringBuilder(64);
            int len = delimiter.length();
            for (int delimiterPos = 0; this.undecodedChunk.isReadable() && delimiterPos < len; ++delimiterPos) {
                nextByte = this.undecodedChunk.readByte();
                if (nextByte == delimiter.charAt(delimiterPos)) {
                    sb.append((char)nextByte);
                    continue;
                }
                this.undecodedChunk.readerIndex(readerIndex);
                throw new NotEnoughDataDecoderException();
            }
            if (this.undecodedChunk.isReadable()) {
                nextByte = this.undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = this.undecodedChunk.readByte();
                    if (nextByte == 10) {
                        return sb.toString();
                    }
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new NotEnoughDataDecoderException();
                }
                if (nextByte == 10) {
                    return sb.toString();
                }
                if (nextByte == 45) {
                    sb.append('-');
                    nextByte = this.undecodedChunk.readByte();
                    if (nextByte == 45) {
                        sb.append('-');
                        if (this.undecodedChunk.isReadable()) {
                            nextByte = this.undecodedChunk.readByte();
                            if (nextByte == 13) {
                                nextByte = this.undecodedChunk.readByte();
                                if (nextByte == 10) {
                                    return sb.toString();
                                }
                                this.undecodedChunk.readerIndex(readerIndex);
                                throw new NotEnoughDataDecoderException();
                            }
                            if (nextByte == 10) {
                                return sb.toString();
                            }
                            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                            return sb.toString();
                        }
                        return sb.toString();
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e);
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new NotEnoughDataDecoderException();
    }

    private String readDelimiter(String delimiter) throws NotEnoughDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
            return this.readDelimiterStandard(delimiter);
        }
        int readerIndex = this.undecodedChunk.readerIndex();
        int len = delimiter.length();
        try {
            byte nextByte;
            StringBuilder sb = new StringBuilder(64);
            for (int delimiterPos = 0; sao.pos < sao.limit && delimiterPos < len; ++delimiterPos) {
                if ((nextByte = sao.bytes[sao.pos++]) == delimiter.charAt(delimiterPos)) {
                    sb.append((char)nextByte);
                    continue;
                }
                this.undecodedChunk.readerIndex(readerIndex);
                throw new NotEnoughDataDecoderException();
            }
            if (sao.pos < sao.limit) {
                if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                    if (sao.pos < sao.limit) {
                        if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                            sao.setReadPosition(0);
                            return sb.toString();
                        }
                        this.undecodedChunk.readerIndex(readerIndex);
                        throw new NotEnoughDataDecoderException();
                    }
                    this.undecodedChunk.readerIndex(readerIndex);
                    throw new NotEnoughDataDecoderException();
                }
                if (nextByte == 10) {
                    sao.setReadPosition(0);
                    return sb.toString();
                }
                if (nextByte == 45) {
                    sb.append('-');
                    if (sao.pos < sao.limit && (nextByte = sao.bytes[sao.pos++]) == 45) {
                        sb.append('-');
                        if (sao.pos < sao.limit) {
                            if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                                if (sao.pos < sao.limit) {
                                    if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                                        sao.setReadPosition(0);
                                        return sb.toString();
                                    }
                                    this.undecodedChunk.readerIndex(readerIndex);
                                    throw new NotEnoughDataDecoderException();
                                }
                                this.undecodedChunk.readerIndex(readerIndex);
                                throw new NotEnoughDataDecoderException();
                            }
                            if (nextByte == 10) {
                                sao.setReadPosition(0);
                                return sb.toString();
                            }
                            sao.setReadPosition(1);
                            return sb.toString();
                        }
                        sao.setReadPosition(0);
                        return sb.toString();
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e);
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new NotEnoughDataDecoderException();
    }

    private void readFileUploadByteMultipartStandard(String delimiter) throws NotEnoughDataDecoderException, ErrorDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        boolean newLine = true;
        int index = 0;
        int lastPosition = this.undecodedChunk.readerIndex();
        boolean found = false;
        while (this.undecodedChunk.isReadable()) {
            byte nextByte = this.undecodedChunk.readByte();
            if (newLine) {
                if (nextByte == delimiter.codePointAt(index)) {
                    if (delimiter.length() != ++index) continue;
                    found = true;
                    break;
                }
                newLine = false;
                index = 0;
                if (nextByte == 13) {
                    if (!this.undecodedChunk.isReadable()) continue;
                    nextByte = this.undecodedChunk.readByte();
                    if (nextByte == 10) {
                        newLine = true;
                        index = 0;
                        lastPosition = this.undecodedChunk.readerIndex() - 2;
                        continue;
                    }
                    lastPosition = this.undecodedChunk.readerIndex() - 1;
                    this.undecodedChunk.readerIndex(lastPosition);
                    continue;
                }
                if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastPosition = this.undecodedChunk.readerIndex() - 1;
                    continue;
                }
                lastPosition = this.undecodedChunk.readerIndex();
                continue;
            }
            if (nextByte == 13) {
                if (!this.undecodedChunk.isReadable()) continue;
                nextByte = this.undecodedChunk.readByte();
                if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastPosition = this.undecodedChunk.readerIndex() - 2;
                    continue;
                }
                lastPosition = this.undecodedChunk.readerIndex() - 1;
                this.undecodedChunk.readerIndex(lastPosition);
                continue;
            }
            if (nextByte == 10) {
                newLine = true;
                index = 0;
                lastPosition = this.undecodedChunk.readerIndex() - 1;
                continue;
            }
            lastPosition = this.undecodedChunk.readerIndex();
        }
        ByteBuf buffer = this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex);
        if (found) {
            try {
                this.currentFileUpload.addContent(buffer, true);
                this.undecodedChunk.readerIndex(lastPosition);
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
        } else {
            try {
                this.currentFileUpload.addContent(buffer, false);
                this.undecodedChunk.readerIndex(lastPosition);
                throw new NotEnoughDataDecoderException();
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
        }
    }

    private void readFileUploadByteMultipart(String delimiter) throws NotEnoughDataDecoderException, ErrorDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
            this.readFileUploadByteMultipartStandard(delimiter);
            return;
        }
        int readerIndex = this.undecodedChunk.readerIndex();
        boolean newLine = true;
        int index = 0;
        int lastrealpos = sao.pos;
        boolean found = false;
        while (sao.pos < sao.limit) {
            byte nextByte = sao.bytes[sao.pos++];
            if (newLine) {
                if (nextByte == delimiter.codePointAt(index)) {
                    if (delimiter.length() != ++index) continue;
                    found = true;
                    break;
                }
                newLine = false;
                index = 0;
                if (nextByte == 13) {
                    if (sao.pos >= sao.limit) continue;
                    if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                        newLine = true;
                        index = 0;
                        lastrealpos = sao.pos - 2;
                        continue;
                    }
                    lastrealpos = --sao.pos;
                    continue;
                }
                if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastrealpos = sao.pos - 1;
                    continue;
                }
                lastrealpos = sao.pos;
                continue;
            }
            if (nextByte == 13) {
                if (sao.pos >= sao.limit) continue;
                if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                    newLine = true;
                    index = 0;
                    lastrealpos = sao.pos - 2;
                    continue;
                }
                lastrealpos = --sao.pos;
                continue;
            }
            if (nextByte == 10) {
                newLine = true;
                index = 0;
                lastrealpos = sao.pos - 1;
                continue;
            }
            lastrealpos = sao.pos;
        }
        int lastPosition = sao.getReadPosition(lastrealpos);
        ByteBuf buffer = this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex);
        if (found) {
            try {
                this.currentFileUpload.addContent(buffer, true);
                this.undecodedChunk.readerIndex(lastPosition);
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
        } else {
            try {
                this.currentFileUpload.addContent(buffer, false);
                this.undecodedChunk.readerIndex(lastPosition);
                throw new NotEnoughDataDecoderException();
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
        }
    }

    private void loadFieldMultipartStandard(String delimiter) throws NotEnoughDataDecoderException, ErrorDataDecoderException {
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            boolean newLine = true;
            int index = 0;
            int lastPosition = this.undecodedChunk.readerIndex();
            boolean found = false;
            while (this.undecodedChunk.isReadable()) {
                byte nextByte = this.undecodedChunk.readByte();
                if (newLine) {
                    if (nextByte == delimiter.codePointAt(index)) {
                        if (delimiter.length() != ++index) continue;
                        found = true;
                        break;
                    }
                    newLine = false;
                    index = 0;
                    if (nextByte == 13) {
                        if (this.undecodedChunk.isReadable()) {
                            nextByte = this.undecodedChunk.readByte();
                            if (nextByte == 10) {
                                newLine = true;
                                index = 0;
                                lastPosition = this.undecodedChunk.readerIndex() - 2;
                                continue;
                            }
                            lastPosition = this.undecodedChunk.readerIndex() - 1;
                            this.undecodedChunk.readerIndex(lastPosition);
                            continue;
                        }
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                        continue;
                    }
                    if (nextByte == 10) {
                        newLine = true;
                        index = 0;
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                        continue;
                    }
                    lastPosition = this.undecodedChunk.readerIndex();
                    continue;
                }
                if (nextByte == 13) {
                    if (this.undecodedChunk.isReadable()) {
                        nextByte = this.undecodedChunk.readByte();
                        if (nextByte == 10) {
                            newLine = true;
                            index = 0;
                            lastPosition = this.undecodedChunk.readerIndex() - 2;
                            continue;
                        }
                        lastPosition = this.undecodedChunk.readerIndex() - 1;
                        this.undecodedChunk.readerIndex(lastPosition);
                        continue;
                    }
                    lastPosition = this.undecodedChunk.readerIndex() - 1;
                    continue;
                }
                if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastPosition = this.undecodedChunk.readerIndex() - 1;
                    continue;
                }
                lastPosition = this.undecodedChunk.readerIndex();
            }
            if (found) {
                try {
                    this.currentAttribute.addContent(this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex), true);
                }
                catch (IOException e) {
                    throw new ErrorDataDecoderException(e);
                }
            }
            try {
                this.currentAttribute.addContent(this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex), false);
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
            this.undecodedChunk.readerIndex(lastPosition);
            throw new NotEnoughDataDecoderException();
            this.undecodedChunk.readerIndex(lastPosition);
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e);
        }
    }

    private void loadFieldMultipart(String delimiter) throws NotEnoughDataDecoderException, ErrorDataDecoderException {
        HttpPostBodyUtil.SeekAheadOptimize sao;
        try {
            sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        }
        catch (HttpPostBodyUtil.SeekAheadNoBackArrayException e1) {
            this.loadFieldMultipartStandard(delimiter);
            return;
        }
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            boolean newLine = true;
            int index = 0;
            int lastrealpos = sao.pos;
            boolean found = false;
            while (sao.pos < sao.limit) {
                byte nextByte = sao.bytes[sao.pos++];
                if (newLine) {
                    if (nextByte == delimiter.codePointAt(index)) {
                        if (delimiter.length() != ++index) continue;
                        found = true;
                        break;
                    }
                    newLine = false;
                    index = 0;
                    if (nextByte == 13) {
                        if (sao.pos >= sao.limit) continue;
                        if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                            newLine = true;
                            index = 0;
                            lastrealpos = sao.pos - 2;
                            continue;
                        }
                        lastrealpos = --sao.pos;
                        continue;
                    }
                    if (nextByte == 10) {
                        newLine = true;
                        index = 0;
                        lastrealpos = sao.pos - 1;
                        continue;
                    }
                    lastrealpos = sao.pos;
                    continue;
                }
                if (nextByte == 13) {
                    if (sao.pos >= sao.limit) continue;
                    if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                        newLine = true;
                        index = 0;
                        lastrealpos = sao.pos - 2;
                        continue;
                    }
                    lastrealpos = --sao.pos;
                    continue;
                }
                if (nextByte == 10) {
                    newLine = true;
                    index = 0;
                    lastrealpos = sao.pos - 1;
                    continue;
                }
                lastrealpos = sao.pos;
            }
            int lastPosition = sao.getReadPosition(lastrealpos);
            if (found) {
                try {
                    this.currentAttribute.addContent(this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex), true);
                }
                catch (IOException e) {
                    throw new ErrorDataDecoderException(e);
                }
            }
            try {
                this.currentAttribute.addContent(this.undecodedChunk.copy(readerIndex, lastPosition - readerIndex), false);
            }
            catch (IOException e) {
                throw new ErrorDataDecoderException(e);
            }
            this.undecodedChunk.readerIndex(lastPosition);
            throw new NotEnoughDataDecoderException();
            this.undecodedChunk.readerIndex(lastPosition);
        }
        catch (IndexOutOfBoundsException e) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new NotEnoughDataDecoderException(e);
        }
    }

    private static String cleanString(String field) {
        StringBuilder sb = new StringBuilder(field.length());
        for (int i = 0; i < field.length(); ++i) {
            char nextChar = field.charAt(i);
            if (nextChar == ':') {
                sb.append(32);
                continue;
            }
            if (nextChar == ',') {
                sb.append(32);
                continue;
            }
            if (nextChar == '=') {
                sb.append(32);
                continue;
            }
            if (nextChar == ';') {
                sb.append(32);
                continue;
            }
            if (nextChar == '\t') {
                sb.append(32);
                continue;
            }
            if (nextChar == '\"') continue;
            sb.append(nextChar);
        }
        return sb.toString().trim();
    }

    private boolean skipOneLine() {
        if (!this.undecodedChunk.isReadable()) {
            return false;
        }
        byte nextByte = this.undecodedChunk.readByte();
        if (nextByte == 13) {
            if (!this.undecodedChunk.isReadable()) {
                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                return false;
            }
            nextByte = this.undecodedChunk.readByte();
            if (nextByte == 10) {
                return true;
            }
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
            return false;
        }
        if (nextByte == 10) {
            return true;
        }
        this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
        return false;
    }

    private static String[] splitHeaderContentType(String sb) {
        int aStart = HttpPostBodyUtil.findNonWhitespace(sb, 0);
        int aEnd = sb.indexOf(59);
        if (aEnd == -1) {
            return new String[]{sb, ""};
        }
        if (sb.charAt(aEnd - 1) == ' ') {
            --aEnd;
        }
        int bStart = HttpPostBodyUtil.findNonWhitespace(sb, aEnd + 1);
        int bEnd = HttpPostBodyUtil.findEndOfString(sb);
        return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd)};
    }

    private static String[] splitMultipartHeader(String sb) {
        int colonEnd;
        int nameStart;
        char ch;
        int nameEnd;
        ArrayList<String> headers = new ArrayList<String>(1);
        for (nameEnd = nameStart = HttpPostBodyUtil.findNonWhitespace(sb, 0); nameEnd < sb.length() && (ch = sb.charAt(nameEnd)) != ':' && !Character.isWhitespace(ch); ++nameEnd) {
        }
        for (colonEnd = nameEnd; colonEnd < sb.length(); ++colonEnd) {
            if (sb.charAt(colonEnd) != ':') continue;
            ++colonEnd;
            break;
        }
        int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
        int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
        headers.add(sb.substring(nameStart, nameEnd));
        String svalue = sb.substring(valueStart, valueEnd);
        String[] values = svalue.indexOf(59) >= 0 ? StringUtil.split(svalue, ';') : StringUtil.split(svalue, ',');
        for (String value : values) {
            headers.add(value.trim());
        }
        String[] array = new String[headers.size()];
        for (int i = 0; i < headers.size(); ++i) {
            array[i] = (String)headers.get(i);
        }
        return array;
    }

    public static class IncompatibleDataDecoderException
    extends DecoderException {
        private static final long serialVersionUID = -953268047926250267L;

        public IncompatibleDataDecoderException() {
        }

        public IncompatibleDataDecoderException(String msg) {
            super(msg);
        }

        public IncompatibleDataDecoderException(Throwable cause) {
            super(cause);
        }

        public IncompatibleDataDecoderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class ErrorDataDecoderException
    extends DecoderException {
        private static final long serialVersionUID = 5020247425493164465L;

        public ErrorDataDecoderException() {
        }

        public ErrorDataDecoderException(String msg) {
            super(msg);
        }

        public ErrorDataDecoderException(Throwable cause) {
            super(cause);
        }

        public ErrorDataDecoderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    public static class EndOfDataDecoderException
    extends DecoderException {
        private static final long serialVersionUID = 1336267941020800769L;
    }

    public static class NotEnoughDataDecoderException
    extends DecoderException {
        private static final long serialVersionUID = -7846841864603865638L;

        public NotEnoughDataDecoderException() {
        }

        public NotEnoughDataDecoderException(String msg) {
            super(msg);
        }

        public NotEnoughDataDecoderException(Throwable cause) {
            super(cause);
        }

        public NotEnoughDataDecoderException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    private static enum MultiPartStatus {
        NOTSTARTED,
        PREAMBLE,
        HEADERDELIMITER,
        DISPOSITION,
        FIELD,
        FILEUPLOAD,
        MIXEDPREAMBLE,
        MIXEDDELIMITER,
        MIXEDDISPOSITION,
        MIXEDFILEUPLOAD,
        MIXEDCLOSEDELIMITER,
        CLOSEDELIMITER,
        PREEPILOGUE,
        EPILOGUE;

    }
}

