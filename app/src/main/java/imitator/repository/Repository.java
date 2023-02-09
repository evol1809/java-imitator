package imitator.repository;

import imitator.message.Message;

import java.util.List;

public interface Repository<T> {

    List<Message> getAll();
}