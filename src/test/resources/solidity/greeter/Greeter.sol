pragma solidity ^0.8.4;

// Example taken from https://www.ethereum.org/greeter, also used in
// https://github.com/ethereum/go-ethereum/wiki/Contract-Tutorial#your-first-citizen-the-greeter

import "../common/Mortal.sol";

contract Greeter is Mortal {
    /* define variable greeting of the type string */
    string greeting;

    /* this runs when the contract is executed */
    constructor(string memory _greeting) {
        greeting = _greeting;
    }

    function newGreeting(string memory _greeting) public {
        greeting = _greeting;
    }

    /* main function */
    function greet() public view returns (string memory) {
        return greeting;
    }
}
