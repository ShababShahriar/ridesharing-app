"""ridesharing URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.10/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
from django.conf.urls import url
from django.contrib import admin

from ridesharing.users import views as userViews
from ridesharing.bookings import views as bookingViews
from ridesharing.messages import views as messageViews

urlpatterns = [
    url(r'^admin/', admin.site.urls),
    url(r'^login/', userViews.Login.as_view()),
    url(r'^updateToken/', userViews.StoreRegistrationToken.as_view()),

    url(r'^addbooking/', bookingViews.AddNewBooking.as_view()),
    url(r'^joinbooking/', bookingViews.JoinBooking.as_view()),
    url(r'^getActiveBookingList', bookingViews.GetActiveBookingList.as_view()),

    url(r'^addmessage', messageViews.AddNewMessage.as_view()),
    url(r'^message/', messageViews.SendToTopic.as_view()),
    url(r'^getAllMessages/', messageViews.GetAllMessages.as_view()),
]
