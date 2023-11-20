# Kotlin Saga Pattern Implementation

_it started as a saga study but idk what the hell it is_

## Questions

In case of an `AddItemCommand`:

- should a `ConsumeProduct` or similar command be triggered to the `Product` microservice?
- in case of success, should de `Product` microservice emits a `ProductConsumed` event that triggers
  an `ContinueItemInclusion` command?

in this scenario, I _believe_ it's a Choreography based Saga, but how can I make it explicit?

## TODO

- [ ] create ArchUnit tests
- [ ] includes GitHub actions
