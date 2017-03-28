from django.db.models import *

from ridesharing.bookings.models import Booking
from ridesharing.users.models import User


class Message(Model):
    message_text = TextField()
    from_user = ForeignKey(User)
    related_booking = ForeignKey(Booking)
    created_at = DateTimeField(auto_now_add=True)

