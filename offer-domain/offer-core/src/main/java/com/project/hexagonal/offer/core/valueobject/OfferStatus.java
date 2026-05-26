package com.project.hexagonal.offer.core.valueobject;

public enum OfferStatus {

    DRAFT {
        @Override
        public OfferStatus proceed() {
            return PUBLISHED;
        }

        public OfferStatus cancel() {
            return CANCELLED;
        }
    },
    PUBLISHED {
        @Override
        public OfferStatus proceed() {
            return CLOSED;
        }

        @Override
        public OfferStatus cancel() {
            return CANCELLED;
        }
    },
    UPDATED {
        @Override
        public OfferStatus proceed() {
            return PUBLISHED;
        }

        @Override
        public OfferStatus cancel() {
            return CANCELLED;
        }
    },
    CLOSED {
        @Override
        public OfferStatus proceed() {
            throw new IllegalStateException("Closed Offer cannot be proceed !");
        }

        @Override
        public OfferStatus cancel() {
            throw new IllegalStateException("Closed Offer cannot be cancelled !");
        }
    },
    CANCELLED {
        @Override
        public OfferStatus proceed() {
            throw new IllegalStateException("Cancelled offer cannot be proceed !");
        }

        @Override
        public OfferStatus cancel() {
            throw new IllegalStateException("Cancelled offer cannot be cancelled again !");
        }
    };

    public abstract OfferStatus proceed();

    public abstract OfferStatus cancel();
}
