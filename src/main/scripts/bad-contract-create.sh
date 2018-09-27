#!/bin/bash

kafkacat -qPb localhost -t contract-create <<< '{"id":"1234","foo":"123456","bar":"ABCD"}'
