package com.uangel.acs.rmqif.types;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RmqMessageType {

    public static final String RMQ_MSG_STR_INBOUND_SET_OFFER_REQ    = "msfmp_inbound_set_offer_req";
    public static final String RMQ_MSG_STR_INBOUND_SET_OFFER_RES    = "msfmp_inbound_set_offer_res";
    public static final String RMQ_MSG_STR_INBOUND_GET_ANSWER_REQ   = "msfmp_inbound_get_answer_req";
    public static final String RMQ_MSG_STR_INBOUND_GET_ANSWER_RES   = "msfmp_inbound_get_answer_res";
    public static final String RMQ_MSG_STR_OUTBOUND_GET_OFFER_REQ	= "msfmp_inbound_get_offer_req";
    public static final String RMQ_MSG_STR_OUTBOUND_GET_OFFER_RES	= "msfmp_inbound_get_offer_res";
    public static final String RMQ_MSG_STR_OUTBOUND_SET_ANSWER_REQ	= "msfmp_inbound_set_answer_req";
    public static final String RMQ_MSG_STR_OUTBOUND_SET_ANSWER_RES	= "msfmp_inbound_set_answer_res";
    public static final String RMQ_MSG_STR_NEGO_DONE_REQ	        = "msfmp_nego_done_req";
    public static final String RMQ_MSG_STR_NEGO_DONE_RES	        = "msfmp_nego_done_res";
    public static final String RMQ_MSG_STR_COMMAND_REQ	            = "msfmp_command_req";
    public static final String RMQ_MSG_STR_COMMAND_RES	            = "msfmp_command_res";
    public static final String RMQ_MSG_STR_PLAY_PROMPT_REQ	        = "msfmp_play_prompt_req";
    public static final String RMQ_MSG_STR_PLAY_PROMPT_ACK	        = "msfmp_play_prompt_ack";
    public static final String RMQ_MSG_STR_PLAY_PROMPT_RES	        = "msfmp_play_prompt_res";
    public static final String RMQ_MSG_STR_PLAY_COLLECT_REQ	        = "msfmp_play_collect_req";
    public static final String RMQ_MSG_STR_PLAY_COLLECT_ACK	        = "msfmp_play_collect_ack";
    public static final String RMQ_MSG_STR_PLAY_COLLECT_RES    	    = "msfmp_play_collect_res";
    public static final String RMQ_MSG_STR_PLAY_RECORD_REQ	        = "msfmp_play_record_req";
    public static final String RMQ_MSG_STR_PLAY_RECORD_ACK     	    = "msfmp_play_record_ack";
    public static final String RMQ_MSG_STR_PLAY_RECORD_RES	        = "msfmp_play_record_res";
    public static final String RMQ_MSG_STR_STOP_RECORD_REQ     	    = "msfmp_stop_record_req";
    public static final String RMQ_MSG_STR_STOP_RECORD_RES     	    = "msfmp_stop_record_res";
    public static final String RMQ_MSG_STR_CONTROL_FILE_REQ    	    = "msfmp_control_file_req";
    public static final String RMQ_MSG_STR_CONTROL_FILE_RES    	    = "msfmp_control_file_res";
    public static final String RMQ_MSG_STR_PLAY_ASR_REQ	            = "msfmp_play_asr_req";
    public static final String RMQ_MSG_STR_PLAY_ASR_ACK    	        = "msfmp_play_asr_ack";
    public static final String RMQ_MSG_STR_PLAY_ASR_RES	            = "msfmp_play_asr_res";
    public static final String RMQ_MSG_STR_STOP_PLAY_REQ            = "msfmp_stop_play_req";
    public static final String RMQ_MSG_STR_STOP_PLAY_RES	        = "msfmp_stop_play_res";
    public static final String RMQ_MSG_STR_HANGUP_REQ	            = "msfmp_hangup_req";
    public static final String RMQ_MSG_STR_HANGUP_RES	            = "msfmp_hangup_res";
    public static final String RMQ_MSG_STR_LONGCALL_CHECK_REQ	    = "msfmp_longcall_check_req";
    public static final String RMQ_MSG_STR_LONGCALL_CHECK_RES	    = "msfmp_longcall_check_res";
    public static final String RMQ_MSG_STR_CREATE_CONFERENCE_REQ	= "msfmp_create_conference_req";
    public static final String RMQ_MSG_STR_CREATE_CONFERENCE_RES	= "msfmp_create_conference_res";
    public static final String RMQ_MSG_STR_DELETE_CONFERENCE_REQ	= "msfmp_delete_conference_req";
    public static final String RMQ_MSG_STR_DELETE_CONFERENCE_RES	= "msfmp_delete_conference_res";
    public static final String RMQ_MSG_STR_JOIN_CONFERENCE_REQ	    = "msfmp_join_conference_req";
    public static final String RMQ_MSG_STR_JOIN_CONFERENCE_RES	    = "msfmp_join_conference_res";
    public static final String RMQ_MSG_STR_WITHDRAW_CONFERENCE_REQ	= "msfmp_withdraw_conference_req";
    public static final String RMQ_MSG_STR_WITHDRAW_CONFERENCE_RES	= "msfmp_withdraw_conference_res";
    public static final String RMQ_MSG_STR_UPDATE_CONFERENCE_REQ	= "msfmp_update_conference_req";
    public static final String RMQ_MSG_STR_UPDATE_CONFERENCE_RES	= "msfmp_update_conference_res";
    public static final String RMQ_MSG_STR_STARTRECORD_CONFERENCE_REQ	= "msfmp_start_record_conference_req";
    public static final String RMQ_MSG_STR_STARTRECORD_CONFERENCE_RES	= "msfmp_start_record_conference_res";
    public static final String RMQ_MSG_STR_STOPRECORD_CONFERENCE_REQ	= "msfmp_stop_record_conference_req";
    public static final String RMQ_MSG_STR_STOPRECORD_CONFERENCE_RES	= "msfmp_stop_record_conference_res";
    public static final String RMQ_MSG_STR_RECORD_CONFERENCE_RPT	= "msfmp_record_conference_rpt";
    public static final String RMQ_MSG_STR_RECORD_CONFERENCE_ACK	= "msfmp_record_conference_ack";
    public static final String RMQ_MSG_STR_CHANGE_CONFERENCE_REQ	= "msfmp_change_conference_req";
    public static final String RMQ_MSG_STR_CHANGE_CONFERENCE_RES	= "msfmp_change_conference_res";
    public static final String RMQ_MSG_STR_DTMF_CONFERENCE_RPT	    = "msfmp_dtmf_conference_rpt";
    public static final String RMQ_MSG_STR_DTMF_CONFERENCE_ACK      = "msfmp_dtmf_conference_ack";
    public static final String RMQ_MSG_STR_PLAY_CONFERENCE_REQ      = "msfmp_play_conference_req";
    public static final String RMQ_MSG_STR_PLAY_CONFERENCE_ACK      = "msfmp_play_conference_ack";
    public static final String RMQ_MSG_STR_PLAY_CONFERENCE_RES      = "msfmp_play_conference_res";

    public static final int RMQ_MSG_TYPE_UNDEFINED = 0;
    public static final int RMQ_MSG_TYPE_INBOUND_SET_OFFER_REQ = 0x0001;
    public static final int RMQ_MSG_TYPE_INBOUND_SET_OFFER_RES = 0x1001;
    public static final int RMQ_MSG_TYPE_INBOUND_GET_ANSWER_REQ = 0x0002;
    public static final int RMQ_MSG_TYPE_INBOUND_GET_ANSWER_RES = 0x1002;
    public static final int RMQ_MSG_TYPE_OUTBOUND_GET_OFFER_REQ = 0x0003;
    public static final int RMQ_MSG_TYPE_OUTBOUND_GET_OFFER_RES = 0x1003;
    public static final int RMQ_MSG_TYPE_OUTBOUND_SET_ANSWER_REQ = 0x0004;
    public static final int RMQ_MSG_TYPE_OUTBOUND_SET_ANSWER_RES = 0x1004;

    public static final int RMQ_MSG_TYPE_HANGUP_REQ = 0x0005;
    public static final int RMQ_MSG_TYPE_HANGUP_RES = 0x1005;

    public static final int RMQ_MSG_TYPE_NEGO_DONE_REQ = 0x0006;
    public static final int RMQ_MSG_TYPE_NEGO_DONE_RES = 0x1006;
    public static final int RMQ_MSG_TYPE_COMMAND_REQ = 0x0007;
    public static final int RMQ_MSG_TYPE_COMMAND_RES = 0x1007;
    public static final int RMQ_MSG_TYPE_LONGCALL_CHECK_REQ = 0x0008;
    public static final int RMQ_MSG_TYPE_LONGCALL_CHECK_RES = 0x1008;

    public static final int RMQ_MSG_TYPE_PLAY_PROMPT_REQ = 0x0011;
    public static final int RMQ_MSG_TYPE_PLAY_PROMPT_RES = 0x1011;
    public static final int RMQ_MSG_TYPE_PLAY_PROMPT_ACK = 0x2011;
    public static final int RMQ_MSG_TYPE_PLAY_COLLECT_REQ = 0x0012;
    public static final int RMQ_MSG_TYPE_PLAY_COLLECT_RES = 0x1012;
    public static final int RMQ_MSG_TYPE_PLAY_COLLECT_ACK = 0x2012;
    public static final int RMQ_MSG_TYPE_PLAY_RECORD_REQ = 0x0013;
    public static final int RMQ_MSG_TYPE_PLAY_RECORD_RES = 0x1013;
    public static final int RMQ_MSG_TYPE_PLAY_RECORD_ACK = 0x2013;
    public static final int RMQ_MSG_TYPE_STOP_PLAY_REQ = 0x0014;
    public static final int RMQ_MSG_TYPE_STOP_PLAY_RES = 0x1014;
    public static final int RMQ_MSG_TYPE_STOP_RECORD_REQ = 0x0015;
    public static final int RMQ_MSG_TYPE_STOP_RECORD_RES = 0x1015;
    public static final int RMQ_MSG_TYPE_CONTROL_FILE_REQ = 0x0016;
    public static final int RMQ_MSG_TYPE_CONTROL_FILE_RES = 0x1016;
    public static final int RMQ_MSG_TYPE_PLAY_ASR_REQ = 0x0017;
    public static final int RMQ_MSG_TYPE_PLAY_ASR_RES = 0x1017;
    public static final int RMQ_MSG_TYPE_PLAY_ASR_ACK = 0x2017;

    public static final int RMQ_MSG_TYPE_CREATE_CONFERENCE_REQ = 0x0021;
    public static final int RMQ_MSG_TYPE_CREATE_CONFERENCE_RES = 0x1021;
    public static final int RMQ_MSG_TYPE_DELETE_CONFERENCE_REQ = 0x0022;
    public static final int RMQ_MSG_TYPE_DELETE_CONFERENCE_RES = 0x1022;
    public static final int RMQ_MSG_TYPE_JOIN_CONFERENCE_REQ = 0x0023;
    public static final int RMQ_MSG_TYPE_JOIN_CONFERENCE_RES = 0x1023;
    public static final int RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_REQ = 0x0024;
    public static final int RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_RES = 0x1024;
    public static final int RMQ_MSG_TYPE_UPDATE_CONFERENCE_REQ = 0x0025;
    public static final int RMQ_MSG_TYPE_UPDATE_CONFERENCE_RES = 0x1025;
    public static final int RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_REQ = 0x0026;
    public static final int RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_RES = 0x1026;
    public static final int RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_REQ = 0x0027;
    public static final int RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_RES = 0x1027;
    public static final int RMQ_MSG_TYPE_RECORD_CONFERENCE_RPT = 0x0028;
    public static final int RMQ_MSG_TYPE_RECORD_CONFERENCE_ACK = 0x2028;
    public static final int RMQ_MSG_TYPE_CHANGE_CONFERENCE_REQ = 0x0029;
    public static final int RMQ_MSG_TYPE_CHANGE_CONFERENCE_RES = 0x1029;
    public static final int RMQ_MSG_TYPE_DTMF_CONFERENCE_RPT = 0x002a;
    public static final int RMQ_MSG_TYPE_DTMF_CONFERENCE_ACK = 0x202a;
    public static final int RMQ_MSG_TYPE_PLAY_CONFERENCE_REQ = 0x002b;
    public static final int RMQ_MSG_TYPE_PLAY_CONFERENCE_RES = 0x102b;
    public static final int RMQ_MSG_TYPE_PLAY_CONFERENCE_ACK = 0x202b;

    public static final int RMQ_MSG_TYPE_HEARTBEAT = 0x0091;

    public static final int RMQ_MSG_COMMON_REASON_CODE_SUCCESS = 0;
    public static final int RMQ_MSG_COMMON_REASON_CODE_FAILURE = -1;
    public static final int RMQ_COMMON_REASON_CODE_TIMEOUT = -2;
    public static final int RMQ_MSG_COMMON_REASON_CODE_WRONG_PARAM = -3;
    public static final int RMQ_MSG_COMMON_REASON_CODE_ALREADY_EXIST = -4;

    private static Map<String, Integer> typeMap() {
        return Collections.unmodifiableMap(Stream.of(
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_INBOUND_SET_OFFER_REQ, RMQ_MSG_TYPE_INBOUND_SET_OFFER_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_INBOUND_SET_OFFER_RES, RMQ_MSG_TYPE_INBOUND_SET_OFFER_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_INBOUND_GET_ANSWER_REQ, RMQ_MSG_TYPE_INBOUND_GET_ANSWER_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_INBOUND_GET_ANSWER_RES, RMQ_MSG_TYPE_INBOUND_GET_ANSWER_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_OUTBOUND_GET_OFFER_REQ, RMQ_MSG_TYPE_OUTBOUND_GET_OFFER_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_OUTBOUND_GET_OFFER_RES, RMQ_MSG_TYPE_OUTBOUND_GET_OFFER_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_OUTBOUND_SET_ANSWER_REQ, RMQ_MSG_TYPE_OUTBOUND_SET_ANSWER_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_OUTBOUND_SET_ANSWER_RES, RMQ_MSG_TYPE_OUTBOUND_SET_ANSWER_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_NEGO_DONE_REQ, RMQ_MSG_TYPE_NEGO_DONE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_NEGO_DONE_RES, RMQ_MSG_TYPE_NEGO_DONE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_REQ, RMQ_MSG_TYPE_COMMAND_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_COMMAND_RES, RMQ_MSG_TYPE_COMMAND_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_PROMPT_REQ, RMQ_MSG_TYPE_PLAY_PROMPT_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_PROMPT_ACK, RMQ_MSG_TYPE_PLAY_PROMPT_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_PROMPT_RES, RMQ_MSG_TYPE_PLAY_PROMPT_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_COLLECT_REQ, RMQ_MSG_TYPE_PLAY_COLLECT_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_COLLECT_ACK, RMQ_MSG_TYPE_PLAY_COLLECT_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_COLLECT_RES, RMQ_MSG_TYPE_PLAY_COLLECT_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_RECORD_REQ, RMQ_MSG_TYPE_PLAY_RECORD_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_RECORD_ACK, RMQ_MSG_TYPE_PLAY_RECORD_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_RECORD_RES, RMQ_MSG_TYPE_PLAY_RECORD_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOP_RECORD_REQ, RMQ_MSG_TYPE_STOP_RECORD_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOP_RECORD_RES, RMQ_MSG_TYPE_STOP_RECORD_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CONTROL_FILE_REQ, RMQ_MSG_TYPE_CONTROL_FILE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CONTROL_FILE_RES, RMQ_MSG_TYPE_CONTROL_FILE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_ASR_REQ, RMQ_MSG_TYPE_PLAY_ASR_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_ASR_ACK, RMQ_MSG_TYPE_PLAY_ASR_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_ASR_RES, RMQ_MSG_TYPE_PLAY_ASR_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOP_PLAY_REQ, RMQ_MSG_TYPE_STOP_PLAY_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOP_PLAY_RES, RMQ_MSG_TYPE_STOP_PLAY_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_HANGUP_REQ, RMQ_MSG_TYPE_HANGUP_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_HANGUP_RES, RMQ_MSG_TYPE_HANGUP_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_LONGCALL_CHECK_REQ, RMQ_MSG_TYPE_LONGCALL_CHECK_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_LONGCALL_CHECK_RES, RMQ_MSG_TYPE_LONGCALL_CHECK_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CREATE_CONFERENCE_REQ, RMQ_MSG_TYPE_CREATE_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CREATE_CONFERENCE_RES, RMQ_MSG_TYPE_CREATE_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DELETE_CONFERENCE_REQ, RMQ_MSG_TYPE_DELETE_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DELETE_CONFERENCE_RES, RMQ_MSG_TYPE_DELETE_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_JOIN_CONFERENCE_REQ, RMQ_MSG_TYPE_JOIN_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_JOIN_CONFERENCE_RES, RMQ_MSG_TYPE_JOIN_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_WITHDRAW_CONFERENCE_REQ, RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_WITHDRAW_CONFERENCE_RES, RMQ_MSG_TYPE_WITHDRAW_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_UPDATE_CONFERENCE_REQ, RMQ_MSG_TYPE_UPDATE_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_UPDATE_CONFERENCE_RES, RMQ_MSG_TYPE_UPDATE_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STARTRECORD_CONFERENCE_REQ, RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STARTRECORD_CONFERENCE_RES, RMQ_MSG_TYPE_STARTRECORD_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOPRECORD_CONFERENCE_REQ, RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_STOPRECORD_CONFERENCE_RES, RMQ_MSG_TYPE_STOPRECORD_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_RECORD_CONFERENCE_RPT, RMQ_MSG_TYPE_RECORD_CONFERENCE_RPT),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_RECORD_CONFERENCE_ACK, RMQ_MSG_TYPE_RECORD_CONFERENCE_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CHANGE_CONFERENCE_REQ, RMQ_MSG_TYPE_CHANGE_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_CHANGE_CONFERENCE_RES, RMQ_MSG_TYPE_CHANGE_CONFERENCE_RES),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DTMF_CONFERENCE_RPT, RMQ_MSG_TYPE_DTMF_CONFERENCE_RPT),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_DTMF_CONFERENCE_ACK, RMQ_MSG_TYPE_DTMF_CONFERENCE_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_CONFERENCE_REQ, RMQ_MSG_TYPE_PLAY_CONFERENCE_REQ),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_CONFERENCE_ACK, RMQ_MSG_TYPE_PLAY_CONFERENCE_ACK),
                new AbstractMap.SimpleEntry<>(RMQ_MSG_STR_PLAY_CONFERENCE_RES, RMQ_MSG_TYPE_PLAY_CONFERENCE_RES)

                ).collect(Collectors.toMap((e) -> e.getKey(), (e) ->e.getValue())));
    }


    public static int getMessageType(String typeStr) {
        Integer value = typeMap().get(typeStr);
        return (value == null) ? RMQ_MSG_TYPE_UNDEFINED : value.intValue();
    }
}
