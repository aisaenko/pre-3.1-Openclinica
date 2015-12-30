#!/bin/sh

iptables -t nat -A PREROUTING -p tcp --dport 443 -j REDIRECT --to-port 8443
iptables-save > /etc/sysconfig/iptables
