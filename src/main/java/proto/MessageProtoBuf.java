// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: MessageProtoBuf.proto

package proto;

public final class MessageProtoBuf {
  private MessageProtoBuf() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface ProtoMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:proto.ProtoMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>required .proto.ProtoMessage.Type type = 1 [default = CHAT];</code>
     */
    boolean hasType();
    /**
     * <code>required .proto.ProtoMessage.Type type = 1 [default = CHAT];</code>
     */
    ProtoMessage.Type getType();

    /**
     * <code>optional string from = 2;</code>
     */
    boolean hasFrom();
    /**
     * <code>optional string from = 2;</code>
     */
    String getFrom();
    /**
     * <code>optional string from = 2;</code>
     */
    com.google.protobuf.ByteString
        getFromBytes();

    /**
     * <code>optional string to = 3;</code>
     */
    boolean hasTo();
    /**
     * <code>optional string to = 3;</code>
     */
    String getTo();
    /**
     * <code>optional string to = 3;</code>
     */
    com.google.protobuf.ByteString
        getToBytes();

    /**
     * <code>required string time = 4;</code>
     */
    boolean hasTime();
    /**
     * <code>required string time = 4;</code>
     */
    String getTime();
    /**
     * <code>required string time = 4;</code>
     */
    com.google.protobuf.ByteString
        getTimeBytes();

    /**
     * <code>required string body = 5;</code>
     */
    boolean hasBody();
    /**
     * <code>required string body = 5;</code>
     */
    String getBody();
    /**
     * <code>required string body = 5;</code>
     */
    com.google.protobuf.ByteString
        getBodyBytes();
  }
  /**
   * Protobuf type {@code proto.ProtoMessage}
   */
  public static final class ProtoMessage extends
      com.google.protobuf.GeneratedMessage implements
      // @@protoc_insertion_point(message_implements:proto.ProtoMessage)
      ProtoMessageOrBuilder {
    // Use ProtoMessage.newBuilder() to construct.
    private ProtoMessage(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private ProtoMessage(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final ProtoMessage defaultInstance;
    public static ProtoMessage getDefaultInstance() {
      return defaultInstance;
    }

    public ProtoMessage getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private ProtoMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              int rawValue = input.readEnum();
              Type value = Type.valueOf(rawValue);
              if (value == null) {
                unknownFields.mergeVarintField(1, rawValue);
              } else {
                bitField0_ |= 0x00000001;
                type_ = value;
              }
              break;
            }
            case 18: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000002;
              from_ = bs;
              break;
            }
            case 26: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000004;
              to_ = bs;
              break;
            }
            case 34: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000008;
              time_ = bs;
              break;
            }
            case 42: {
              com.google.protobuf.ByteString bs = input.readBytes();
              bitField0_ |= 0x00000010;
              body_ = bs;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return MessageProtoBuf.internal_static_proto_ProtoMessage_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return MessageProtoBuf.internal_static_proto_ProtoMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ProtoMessage.class, Builder.class);
    }

    public static com.google.protobuf.Parser<ProtoMessage> PARSER =
        new com.google.protobuf.AbstractParser<ProtoMessage>() {
      public ProtoMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ProtoMessage(input, extensionRegistry);
      }
    };

    @Override
    public com.google.protobuf.Parser<ProtoMessage> getParserForType() {
      return PARSER;
    }

    /**
     * Protobuf enum {@code proto.ProtoMessage.Type}
     */
    public enum Type
        implements com.google.protobuf.ProtocolMessageEnum {
      /**
       * <code>LOGIN = 0;</code>
       */
      LOGIN(0, 0),
      /**
       * <code>CHAT = 1;</code>
       */
      CHAT(1, 1),
      /**
       * <code>LOGIN_RESPONSE = 2;</code>
       */
      LOGIN_RESPONSE(2, 2),
      /**
       * <code>CHAT_RESPONSE = 3;</code>
       */
      CHAT_RESPONSE(3, 3),
      /**
       * <code>JOIN_GROUP_NOTIFY = 4;</code>
       */
      JOIN_GROUP_NOTIFY(4, 4),
      /**
       * <code>QUIT_GROUP_NOTIFY = 5;</code>
       */
      QUIT_GROUP_NOTIFY(5, 5),
      /**
       * <code>SOMEONE_JOIN_NOTIFY = 6;</code>
       */
      SOMEONE_JOIN_NOTIFY(6, 6),
      /**
       * <code>DISMISS_GROUP_NOTIFY = 7;</code>
       */
      DISMISS_GROUP_NOTIFY(7, 7),
      /**
       * <code>UPDATE_GROUP_NOTIFY = 8;</code>
       */
      UPDATE_GROUP_NOTIFY(8, 8),
      /**
       * <code>NO_GROUP_NOTIFY = 9;</code>
       */
      NO_GROUP_NOTIFY(9, 9),
      /**
       * <code>HEART_BEAT = 10;</code>
       */
      HEART_BEAT(10, 10),
      /**
       * <code>HEART_BEAT_RESPONSE = 11;</code>
       */
      HEART_BEAT_RESPONSE(11, 11),
      /**
       * <code>SOMEONE_QUIT_NOTIFY = 12;</code>
       */
      SOMEONE_QUIT_NOTIFY(12, 12),
      ;

      /**
       * <code>LOGIN = 0;</code>
       */
      public static final int LOGIN_VALUE = 0;
      /**
       * <code>CHAT = 1;</code>
       */
      public static final int CHAT_VALUE = 1;
      /**
       * <code>LOGIN_RESPONSE = 2;</code>
       */
      public static final int LOGIN_RESPONSE_VALUE = 2;
      /**
       * <code>CHAT_RESPONSE = 3;</code>
       */
      public static final int CHAT_RESPONSE_VALUE = 3;
      /**
       * <code>JOIN_GROUP_NOTIFY = 4;</code>
       */
      public static final int JOIN_GROUP_NOTIFY_VALUE = 4;
      /**
       * <code>QUIT_GROUP_NOTIFY = 5;</code>
       */
      public static final int QUIT_GROUP_NOTIFY_VALUE = 5;
      /**
       * <code>SOMEONE_JOIN_NOTIFY = 6;</code>
       */
      public static final int SOMEONE_JOIN_NOTIFY_VALUE = 6;
      /**
       * <code>DISMISS_GROUP_NOTIFY = 7;</code>
       */
      public static final int DISMISS_GROUP_NOTIFY_VALUE = 7;
      /**
       * <code>UPDATE_GROUP_NOTIFY = 8;</code>
       */
      public static final int UPDATE_GROUP_NOTIFY_VALUE = 8;
      /**
       * <code>NO_GROUP_NOTIFY = 9;</code>
       */
      public static final int NO_GROUP_NOTIFY_VALUE = 9;
      /**
       * <code>HEART_BEAT = 10;</code>
       */
      public static final int HEART_BEAT_VALUE = 10;
      /**
       * <code>HEART_BEAT_RESPONSE = 11;</code>
       */
      public static final int HEART_BEAT_RESPONSE_VALUE = 11;
      /**
       * <code>SOMEONE_QUIT_NOTIFY = 12;</code>
       */
      public static final int SOMEONE_QUIT_NOTIFY_VALUE = 12;


      public final int getNumber() { return value; }

      public static Type valueOf(int value) {
        switch (value) {
          case 0: return LOGIN;
          case 1: return CHAT;
          case 2: return LOGIN_RESPONSE;
          case 3: return CHAT_RESPONSE;
          case 4: return JOIN_GROUP_NOTIFY;
          case 5: return QUIT_GROUP_NOTIFY;
          case 6: return SOMEONE_JOIN_NOTIFY;
          case 7: return DISMISS_GROUP_NOTIFY;
          case 8: return UPDATE_GROUP_NOTIFY;
          case 9: return NO_GROUP_NOTIFY;
          case 10: return HEART_BEAT;
          case 11: return HEART_BEAT_RESPONSE;
          case 12: return SOMEONE_QUIT_NOTIFY;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<Type>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static com.google.protobuf.Internal.EnumLiteMap<Type>
          internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<Type>() {
              public Type findValueByNumber(int number) {
                return Type.valueOf(number);
              }
            };

      public final com.google.protobuf.Descriptors.EnumValueDescriptor
          getValueDescriptor() {
        return getDescriptor().getValues().get(index);
      }
      public final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptorForType() {
        return getDescriptor();
      }
      public static final com.google.protobuf.Descriptors.EnumDescriptor
          getDescriptor() {
        return ProtoMessage.getDescriptor().getEnumTypes().get(0);
      }

      private static final Type[] VALUES = values();

      public static Type valueOf(
          com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
        if (desc.getType() != getDescriptor()) {
          throw new IllegalArgumentException(
            "EnumValueDescriptor is not for this type.");
        }
        return VALUES[desc.getIndex()];
      }

      private final int index;
      private final int value;

      private Type(int index, int value) {
        this.index = index;
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:proto.ProtoMessage.Type)
    }

    private int bitField0_;
    public static final int TYPE_FIELD_NUMBER = 1;
    private Type type_;
    /**
     * <code>required .proto.ProtoMessage.Type type = 1 [default = CHAT];</code>
     */
    public boolean hasType() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required .proto.ProtoMessage.Type type = 1 [default = CHAT];</code>
     */
    public Type getType() {
      return type_;
    }

    public static final int FROM_FIELD_NUMBER = 2;
    private Object from_;
    /**
     * <code>optional string from = 2;</code>
     */
    public boolean hasFrom() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>optional string from = 2;</code>
     */
    public String getFrom() {
      Object ref = from_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          from_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string from = 2;</code>
     */
    public com.google.protobuf.ByteString
        getFromBytes() {
      Object ref = from_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        from_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TO_FIELD_NUMBER = 3;
    private Object to_;
    /**
     * <code>optional string to = 3;</code>
     */
    public boolean hasTo() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional string to = 3;</code>
     */
    public String getTo() {
      Object ref = to_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          to_ = s;
        }
        return s;
      }
    }
    /**
     * <code>optional string to = 3;</code>
     */
    public com.google.protobuf.ByteString
        getToBytes() {
      Object ref = to_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        to_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TIME_FIELD_NUMBER = 4;
    private Object time_;
    /**
     * <code>required string time = 4;</code>
     */
    public boolean hasTime() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>required string time = 4;</code>
     */
    public String getTime() {
      Object ref = time_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          time_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string time = 4;</code>
     */
    public com.google.protobuf.ByteString
        getTimeBytes() {
      Object ref = time_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        time_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int BODY_FIELD_NUMBER = 5;
    private Object body_;
    /**
     * <code>required string body = 5;</code>
     */
    public boolean hasBody() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    /**
     * <code>required string body = 5;</code>
     */
    public String getBody() {
      Object ref = body_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          body_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string body = 5;</code>
     */
    public com.google.protobuf.ByteString
        getBodyBytes() {
      Object ref = body_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        body_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private void initFields() {
      type_ = Type.CHAT;
      from_ = "";
      to_ = "";
      time_ = "";
      body_ = "";
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      if (!hasType()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasTime()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasBody()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeEnum(1, type_.getNumber());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getFromBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(3, getToBytes());
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeBytes(4, getTimeBytes());
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        output.writeBytes(5, getBodyBytes());
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeEnumSize(1, type_.getNumber());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getFromBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(3, getToBytes());
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(4, getTimeBytes());
      }
      if (((bitField0_ & 0x00000010) == 0x00000010)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(5, getBodyBytes());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    protected Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static ProtoMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtoMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtoMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static ProtoMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static ProtoMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static ProtoMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static ProtoMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static ProtoMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static ProtoMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static ProtoMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(ProtoMessage prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code proto.ProtoMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:proto.ProtoMessage)
        ProtoMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return MessageProtoBuf.internal_static_proto_ProtoMessage_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return MessageProtoBuf.internal_static_proto_ProtoMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                ProtoMessage.class, Builder.class);
      }

      // Construct using proto.MessageProtoBuf.ProtoMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        type_ = Type.CHAT;
        bitField0_ = (bitField0_ & ~0x00000001);
        from_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        to_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        time_ = "";
        bitField0_ = (bitField0_ & ~0x00000008);
        body_ = "";
        bitField0_ = (bitField0_ & ~0x00000010);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return MessageProtoBuf.internal_static_proto_ProtoMessage_descriptor;
      }

      public ProtoMessage getDefaultInstanceForType() {
        return ProtoMessage.getDefaultInstance();
      }

      public ProtoMessage build() {
        ProtoMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public ProtoMessage buildPartial() {
        ProtoMessage result = new ProtoMessage(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.type_ = type_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.from_ = from_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.to_ = to_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.time_ = time_;
        if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
          to_bitField0_ |= 0x00000010;
        }
        result.body_ = body_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof ProtoMessage) {
          return mergeFrom((ProtoMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(ProtoMessage other) {
        if (other == ProtoMessage.getDefaultInstance()) return this;
        if (other.hasType()) {
          setType(other.getType());
        }
        if (other.hasFrom()) {
          bitField0_ |= 0x00000002;
          from_ = other.from_;
          onChanged();
        }
        if (other.hasTo()) {
          bitField0_ |= 0x00000004;
          to_ = other.to_;
          onChanged();
        }
        if (other.hasTime()) {
          bitField0_ |= 0x00000008;
          time_ = other.time_;
          onChanged();
        }
        if (other.hasBody()) {
          bitField0_ |= 0x00000010;
          body_ = other.body_;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasType()) {
          
          return false;
        }
        if (!hasTime()) {
          
          return false;
        }
        if (!hasBody()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        ProtoMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (ProtoMessage) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private Type type_ = Type.CHAT;
      /**
       * <code>required .proto.ProtoMessage.Type type = 1 [default = CHAT];</code>
       */
      public boolean hasType() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required .proto.ProtoMessage.Type type = 1 [default = CHAT];</code>
       */
      public Type getType() {
        return type_;
      }
      /**
       * <code>required .proto.ProtoMessage.Type type = 1 [default = CHAT];</code>
       */
      public Builder setType(Type value) {
        if (value == null) {
          throw new NullPointerException();
        }
        bitField0_ |= 0x00000001;
        type_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required .proto.ProtoMessage.Type type = 1 [default = CHAT];</code>
       */
      public Builder clearType() {
        bitField0_ = (bitField0_ & ~0x00000001);
        type_ = Type.CHAT;
        onChanged();
        return this;
      }

      private Object from_ = "";
      /**
       * <code>optional string from = 2;</code>
       */
      public boolean hasFrom() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>optional string from = 2;</code>
       */
      public String getFrom() {
        Object ref = from_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            from_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>optional string from = 2;</code>
       */
      public com.google.protobuf.ByteString
          getFromBytes() {
        Object ref = from_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          from_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string from = 2;</code>
       */
      public Builder setFrom(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        from_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string from = 2;</code>
       */
      public Builder clearFrom() {
        bitField0_ = (bitField0_ & ~0x00000002);
        from_ = getDefaultInstance().getFrom();
        onChanged();
        return this;
      }
      /**
       * <code>optional string from = 2;</code>
       */
      public Builder setFromBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        from_ = value;
        onChanged();
        return this;
      }

      private Object to_ = "";
      /**
       * <code>optional string to = 3;</code>
       */
      public boolean hasTo() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>optional string to = 3;</code>
       */
      public String getTo() {
        Object ref = to_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            to_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>optional string to = 3;</code>
       */
      public com.google.protobuf.ByteString
          getToBytes() {
        Object ref = to_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          to_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string to = 3;</code>
       */
      public Builder setTo(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        to_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string to = 3;</code>
       */
      public Builder clearTo() {
        bitField0_ = (bitField0_ & ~0x00000004);
        to_ = getDefaultInstance().getTo();
        onChanged();
        return this;
      }
      /**
       * <code>optional string to = 3;</code>
       */
      public Builder setToBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        to_ = value;
        onChanged();
        return this;
      }

      private Object time_ = "";
      /**
       * <code>required string time = 4;</code>
       */
      public boolean hasTime() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>required string time = 4;</code>
       */
      public String getTime() {
        Object ref = time_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            time_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string time = 4;</code>
       */
      public com.google.protobuf.ByteString
          getTimeBytes() {
        Object ref = time_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          time_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string time = 4;</code>
       */
      public Builder setTime(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
        time_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string time = 4;</code>
       */
      public Builder clearTime() {
        bitField0_ = (bitField0_ & ~0x00000008);
        time_ = getDefaultInstance().getTime();
        onChanged();
        return this;
      }
      /**
       * <code>required string time = 4;</code>
       */
      public Builder setTimeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
        time_ = value;
        onChanged();
        return this;
      }

      private Object body_ = "";
      /**
       * <code>required string body = 5;</code>
       */
      public boolean hasBody() {
        return ((bitField0_ & 0x00000010) == 0x00000010);
      }
      /**
       * <code>required string body = 5;</code>
       */
      public String getBody() {
        Object ref = body_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          if (bs.isValidUtf8()) {
            body_ = s;
          }
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>required string body = 5;</code>
       */
      public com.google.protobuf.ByteString
          getBodyBytes() {
        Object ref = body_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          body_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string body = 5;</code>
       */
      public Builder setBody(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000010;
        body_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string body = 5;</code>
       */
      public Builder clearBody() {
        bitField0_ = (bitField0_ & ~0x00000010);
        body_ = getDefaultInstance().getBody();
        onChanged();
        return this;
      }
      /**
       * <code>required string body = 5;</code>
       */
      public Builder setBodyBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000010;
        body_ = value;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:proto.ProtoMessage)
    }

    static {
      defaultInstance = new ProtoMessage(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:proto.ProtoMessage)
  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_proto_ProtoMessage_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_proto_ProtoMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\025MessageProtoBuf.proto\022\005proto\"\210\003\n\014Proto" +
      "Message\022,\n\004type\030\001 \002(\0162\030.proto.ProtoMessa" +
      "ge.Type:\004CHAT\022\014\n\004from\030\002 \001(\t\022\n\n\002to\030\003 \001(\t\022" +
      "\014\n\004time\030\004 \002(\t\022\014\n\004body\030\005 \002(\t\"\223\002\n\004Type\022\t\n\005" +
      "LOGIN\020\000\022\010\n\004CHAT\020\001\022\022\n\016LOGIN_RESPONSE\020\002\022\021\n" +
      "\rCHAT_RESPONSE\020\003\022\025\n\021JOIN_GROUP_NOTIFY\020\004\022" +
      "\025\n\021QUIT_GROUP_NOTIFY\020\005\022\027\n\023SOMEONE_JOIN_N" +
      "OTIFY\020\006\022\030\n\024DISMISS_GROUP_NOTIFY\020\007\022\027\n\023UPD" +
      "ATE_GROUP_NOTIFY\020\010\022\023\n\017NO_GROUP_NOTIFY\020\t\022" +
      "\016\n\nHEART_BEAT\020\n\022\027\n\023HEART_BEAT_RESPONSE\020\013",
      "\022\027\n\023SOMEONE_QUIT_NOTIFY\020\014"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_proto_ProtoMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_proto_ProtoMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessage.FieldAccessorTable(
        internal_static_proto_ProtoMessage_descriptor,
        new String[] { "Type", "From", "To", "Time", "Body", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
