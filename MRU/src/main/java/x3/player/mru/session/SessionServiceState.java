package x3.player.mru.session;

/**
 * OOS      - Unavailable
 * IDLE     - Initial
 * OFFER    - Received OfferReq
 * ANSWER   - Received AnswerReq
 * PREPARE  - Received NegoDoneReq
 * READY    - Received StartServiceRes
 * PLAY_B   - Received CommandReq and before playing
 * PLAY_C   - Playing state
 * RELEASE  - Sent or received HangupReq
 */
public enum SessionServiceState {

    OOS, IDLE, OFFER, ANSWER, PREPARE, READY, PLAY_B, PLAY_C, RELEASE
}
