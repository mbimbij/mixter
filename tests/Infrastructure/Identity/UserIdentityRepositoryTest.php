<?php

namespace Tests\Infrastructure\Identity;

use App\Domain\Identity\UserId;
use App\Domain\Identity\UserIdentity;
use App\Domain\Identity\UserRegistered;
use App\Infrastructure\Identity\UserIdentityRepository;
use App\Infrastructure\InMemoryEventStore;

class UserIdentityRepositoryTest extends \PHPUnit_Framework_TestCase {
    public function testGivenAUserIsRegistered_WhenGetByUserId_ThenReturnsUserIdentity() {
        $userRegistered = new UserRegistered(new UserId('clem@mix-it.fr'));
        $eventStore = new InMemoryEventStore(array($userRegistered));
        $userIdentityRepository = new UserIdentityRepository($eventStore);

        $userIdentity = $userIdentityRepository->get($userRegistered->getUserId());

        \Assert\that($userIdentity)->notNull();
        $decisionProjection = new \ReflectionProperty('App\Domain\Identity\UserIdentity', 'decisionProjection');
        $decisionProjection->setAccessible(true);
        $userId = new \ReflectionProperty('App\Domain\Identity\UserIdentity\DecisionProjection', 'userId');
        $userId->setAccessible(true);
        \Assert\that($userId->getValue($decisionProjection->getValue($userIdentity)))->eq($userRegistered->getUserId());
    }

    public function testGivenAUserIsNotRegistered_WhenGetByUserId_ThenThrowsUnknownAggregate() {
        $eventStore = new InMemoryEventStore();
        $userIdentityRepository = new UserIdentityRepository($eventStore);

        $this->setExpectedException('App\Domain\UnknownAggregate');

        $userIdentityRepository->get(new UserId("unknown@mix-it.fr"));
    }
}