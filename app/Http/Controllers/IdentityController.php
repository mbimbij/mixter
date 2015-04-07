<?php namespace App\Http\Controllers;

use App\Domain\Identity\UserId;
use App\Domain\Identity\UserIdentity;
use App\Domain\IEventPublisher;
use App\Domain\UnknownAggregate;
use App\Infrastructure\EventPublisher;
use App\Infrastructure\Identity\UserIdentityRepository;
use Illuminate\Support\Facades\Input;
use Illuminate\Support\Facades\Response;

class IdentityController extends Controller {
    /** @var EventPublisher */
    private $eventPublisher;

    /** @var UserIdentityRepository */
    private $userIdentityRepository;

    /**
     * Create a new controller instance.
     * @param EventPublisher $eventPublisher
     * @param UserIdentityRepository $userIdentityRepository
     */
	public function __construct(
        EventPublisher $eventPublisher,
        UserIdentityRepository $userIdentityRepository)
	{
        $this->eventPublisher = $eventPublisher;
        $this->userIdentityRepository = $userIdentityRepository;
    }

	/**
	 * Show the application welcome screen to the user.
	 *
	 * @return Response
	 */
	public function index()
	{
		return view('welcome');
	}

    public function register()
    {
        $email = Input::get('email');
        UserIdentity::register($this->eventPublisher, new UserId($email));
        return response('', 201);
    }

    public function logIn()
    {
        $email = Input::get('email');
        try {
            $userIdentity = $this->userIdentityRepository->get(new UserId($email));
            $userIdentity->logIn($this->eventPublisher);
            return response('Logged in', 200);
        } catch (UnknownAggregate $unknownAggregate) {
            return response('User '.$email.' not authenticated', 401);
        }
    }
}