from django.db.models import *
from django_enumfield import enum

from ridesharing.users.models import User


class RideType(enum.Enum):
    CNG = 'cng'
    UBER = 'uber'


class Booking(Model):
    name = CharField(max_length=50)
    related_topic_name = CharField(max_length=100)
    num_users = IntegerField(default=1)
    is_full = BooleanField(default=False)
    is_active = BooleanField(default=True)
    start_time = DateTimeField()
    from_location = CharField(max_length=30)
    to_location = CharField(max_length=30)
    ride_type = enum.EnumField(RideType, default=RideType.CNG)
    created_by = ForeignKey(User)
    created_at = DateTimeField(auto_now_add=True)


class UserBooking(Model):
    user = ForeignKey(User)
    booking = ForeignKey(Booking)
    created_at = DateTimeField(auto_now_add=True)
