package x3.player.mru.session;

/**
 * OOS      - Unavailable
 * IDLE     - Initial
 * OFFER    - Received OfferReq
 * ANSWER   - Received AnswerReq
 * PREPARE  - Received NegoDoneReq
 * READY    - After all channel opened
 * START    - Received ServiceStartReq
 * PLAY_B   - Received CommandReq(PlayReq)
 * RELEASE  - Sent HangupReq
 */
public enum SessionState {

    OOS, IDLE, OFFER, ANSWER, PREPARE, READY, START, PLAY, RELEASE
}
