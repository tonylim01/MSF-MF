package x3.player.mru.session;

/**
 * OOS      - Unavailable
 * IDLE     - Initial
 * OFFER    - Received OfferReq
 * ANSWER   - Received AnswerReq
 * PREPARE  - Received NegoDoneReq
 * READY    - Received StartServiceRes
 * RELEASE  - Sent or received HangupReq
 */
public enum SessionServiceState {

    OOS, IDLE, OFFER, ANSWER, PREPARE, READY, RELEASE
}
