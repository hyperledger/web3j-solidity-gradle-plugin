pragma solidity >=0.7.0 <0.8.0;

contract Mortal {
    /* Define variable owner of the type address */
    address owner;

    /* This constructor is executed at initialization and sets the owner of the contract */
    constructor() { owner = msg.sender; }

    /* TODO 
    consider to move to openzepeelin pausable, see context on issue: 
    https://ethereum.stackexchange.com/questions/65872/invalid-type-for-argument-in-function-call-invalid-implicit-conversion-from-add
    */
    /* Function to recover the funds on the contract */
    function kill() public { if (msg.sender == owner) selfdestruct(msg.sender); }
}

contract Greeter is Mortal {
    /* Define variable greeting of the type string */
    string greeting;

    /* This runs when the contract is executed */
    constructor(string memory _greeting) {
        greeting = _greeting;
    }

    /* Main function */
    function greet() public view returns (string memory) {
        return greeting;
    }
}
