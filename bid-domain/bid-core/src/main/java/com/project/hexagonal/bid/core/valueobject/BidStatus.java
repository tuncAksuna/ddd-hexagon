package com.project.hexagonal.bid.core.valueobject;

public enum BidStatus {

    SUBMITTED {
        @Override
        public BidStatus accept() {
            return ACCEPTED;
        }

        @Override
        public BidStatus cancel() {
            return CANCELLED;
        }
    },
    ACCEPTED {
        @Override
        public BidStatus accept() {
            throw new IllegalStateException("Accepted bid cannot be accepted again !");
        }

        @Override
        public BidStatus cancel() {
            throw new IllegalStateException("Accepted bid cannot be cancelled !");
        }
    },
    REJECTED {
        @Override
        public BidStatus accept() {
            throw new IllegalStateException("Rejected bid cannot be accepted !");
        }

        @Override
        public BidStatus cancel() {
            throw new IllegalStateException("Rejected bid cannot be cancelled !");
        }
    },
    CANCELLED {
        @Override
        public BidStatus accept() {
            throw new IllegalStateException("Cancelled bid cannot be accepted !");
        }

        @Override
        public BidStatus cancel() {
            throw new IllegalStateException("Cancelled bid cannot be cancelled again !");
        }
    };

    public abstract BidStatus accept();
    public abstract BidStatus cancel();
}
