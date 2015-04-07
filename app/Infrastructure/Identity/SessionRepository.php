<?php

namespace App\Infrastructure\Identity;

use App\Domain\Identity\ISessionRepository;
use App\Domain\Identity\SessionId;
use App\Domain\Identity\SessionProjection;
use App\Domain\Identity\UserId;
use App\Infrastructure\IProjectionStore;

class SessionRepository implements ISessionRepository
{
    const PROJECTION_TYPE = 'App\Domain\Identity\SessionProjection';

    /**
     * @var IProjectionStore
     */
    private $projectionStore;

    public function __construct(IProjectionStore $projectionStore) {
        $this->projectionStore = $projectionStore;
    }

    /**
     * @param SessionId $sessionId
     * @return UserId
     */
    public function getUserIdOfSessionId(SessionId $sessionId)
    {
        /** @var SessionProjection $sessionProjection */
        $sessionProjection = $this->projectionStore->get($sessionId->getId(), self::PROJECTION_TYPE);
        if (is_null($sessionProjection)) {
            return null;
        }
        return $sessionProjection->getUserId();
    }

    public function save(SessionProjection $sessionProjection)
    {
        $this->projectionStore->store($sessionProjection->getSessionId()->getId(), $sessionProjection);
    }

    public function remove(SessionId $sessionId)
    {
        // TODO: Implement remove() method.
    }
}