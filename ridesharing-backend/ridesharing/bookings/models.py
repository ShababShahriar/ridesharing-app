import datetime

from django.db.models import *
from model_utils import Choices
from django_enumfield import enum

from ridesharing.users.models import User


class RideType(enum.Enum):
    CNG = 0
    UBER = 1


class Booking(Model):
    RIDETYPE = Choices('cng', 'uber')
    max_users = {
        'cng': 3,
        'uber': 4,
    }

    related_topic_name = CharField(max_length=100)
    num_users = IntegerField(default=1)
    is_full = BooleanField(default=False)
    is_active = BooleanField(default=True)
    start_time = DateTimeField()
    from_location = CharField(max_length=30)
    to_location = CharField(max_length=30)
    ride_type = CharField(choices=RIDETYPE, default=RIDETYPE.cng, max_length=20)
    created_by = ForeignKey(User)
    created_at = DateTimeField(auto_now_add=True)

    @staticmethod
    def add_booking(**kwargs):
        try:
            booking = Booking.objects.create(**kwargs)
            data = {
                'user': kwargs.get('created_by'),
                'booking': booking
            }
            return UserBooking.add_user_booking(**data)
        except:
            return False

    def add_new_user(self):
        self.num_users += 1
        if self.num_users == self.max_users.get(self.ride_type):
            self.is_full = True
        self.save()

    @staticmethod
    def get_active_booking_list(**kwargs):
        booking_list = Booking.objects.filter(start_time__gt=datetime.datetime.now(), **kwargs).order_by('start_time')
        return booking_list


class UserBooking(Model):
    user = ForeignKey(User)
    booking = ForeignKey(Booking)
    created_at = DateTimeField(auto_now_add=True)

    @staticmethod
    def add_user_booking(**kwargs):
        try:
            UserBooking.objects.create(**kwargs)
            return True
        except:
            return False
