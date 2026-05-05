package com.github.bovvver.infrastructure;

import com.github.bovvver.offermanagment.vo.OfferStatus;

public class OperationNotAllowedInCurrentStateException extends RuntimeException {

    public OperationNotAllowedInCurrentStateException(OfferStatus offerStatus) {
        super("Cannot perform this action on offer with status %s".formatted(offerStatus));
    }
}
