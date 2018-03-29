package x3.player.mru.session;

/**
 * OOS      - Unavailable
 * IDLE     - Initial
 * OFFER    - Received OfferReq
 * ANSWER   - Received AnswerReq
 * PREPARE  - Received NegoDoneReq
 * READY    - After all channel opened
 * PLAY_B   - Received CommandReq and before playing
 * PLAY_C   - Playing state
 * RELEASE  - Sent HangupReq
 */
public enum SessionState {

    OOS, IDLE, OFFER, ANSWER, PREPARE, READY, PLAY_B, PLAY_C, RELEASE
}
