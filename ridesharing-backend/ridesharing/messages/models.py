from django.db.models import *

from ridesharing.bookings.models import Booking
from ridesharing.users.models import User


class Message(Model):
    message_text = TextField()
    from_user = ForeignKey(User)
    related_booking = ForeignKey(Booking)
    created_at = DateTimeField(auto_now_add=True)

    @staticmethod
    def add_message(**kwargs):
        try:
            Message.objects.create(**kwargs)
            return True
        except:
            return False

    @staticmethod
    def get_all_messages(booking_id):
        try:
            booking = Booking.objects.get(id=booking_id)
            return Message.objects.filter(related_booking=booking).order_by('id')
        except Exception as e:
            print e
            return False
